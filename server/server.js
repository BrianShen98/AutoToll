const express = require('express');
const bodyParser = require('body-parser');//used to parse post request
const mysql = require('mysql');
const passwordHash = require('password-hash');
//connect to the database
let pool = mysql.createPool({
	connectionLimit: 100,
	host: "localhost",
	user: "jingyue",
	password: "123456",
	database: 'autotoll'
});


const app = express();
//reutrn middleware that parses json
app.use(bodyParser.json());

//add the user into database
app.post('/register',(req,res)=>{
	let NFC_ID = req.body.NFC_ID;
	let username = req.body.username;
	//prevent exploitation
	if(username == 'null'){
		res.json({status:"Failure",info:"Username cannot be null!"});
		return;
	}
	let password = passwordHash.generate(req.body.password);

	pool.getConnection((err,connection)=>{
		if(err){
			res.json({status:"Failure",info:"Error in database connection"});
			return;
		}
		
		connection.query( "SELECT * FROM Users WHERE NFC_ID = \""+NFC_ID+"\"" ,(error,result,fields)=>{
			if(error) throw error

			//if NFC_ID does not exist
			if(result.length == 0){
				res.json({status:"Failure",info:"The NFC_ID does not exist!"});
				connection.release();
			}
			else{
				//if the user has already registered
				if(result[0].Username !== null){
					res.json({status:"Failure",info:"You've already registered"});
					connection.release();					
				}
				
				else{
					connection.query("SELECT Username from Users WHERE Username = \"" + username+"\"",(err,result, fields)=>{
						if(err) throw err;
						//if username already exists
						if(result.length != 0){
							res.json({status:"Failure",info:"Username already exists!"});
							connection.release();							
						}
						//update the user info
						else{
							connection.query("UPDATE Users SET Username = \""+username+"\", Password = \""+password+"\" , Balance = 0 WHERE NFC_ID = \""+NFC_ID+"\"",(err,result,fields)=>{
								if(err) throw err;
								res.json({status:"Success",info:"Registration completed!"});
								connection.release();
							})
						}

					})
				}
			}
		});
			
	});
});

//listening for Payment request, update database, decide whether to release the user
app.post('/deposit',(req,res)=>{
	let amount = parseInt(req.body.amount);
	let username = req.body.username;
	pool.getConnection((err,connection)=>{
		
		if(err){
			res.json({status: "Failure",info:"Error in database connection"});
			return;
		}
		//check if user does not exists
		connection.query("SELECT * FROM Users WHERE Username = \""+username+"\"",(error,result,fields)=>{
			if(error) throw error;
			if(result.length == 0){
				res.json({status:"Failure", info: "User does not exist!"});
				connection.release();
			}
			//add money to user's balance
			else{
				new_balance = result[0].Balance + amount;
				
				connection.query("UPDATE Users SET Balance = "+ new_balance+" WHERE Username = \""+username+"\"",(error,result,field)=>{
					if (error) throw error;
					res.json({status:"Success",info:"Deposit completed!", balance:new_balance});
					connection.release();
				})				
			}	
						
		});


	});
});

//receive request from arduino to check if the car can pass or not
app.get("/check",(req,res)=>{
	let NFC_ID = req.query.NFC_ID;
	pool.getConnection((err,connection)=>{
		if(err){
			res.json({status:"Failure",info:"Error in database connction!"});
			return;
		}
		connection.query("SELECT Balance from Users WHERE NFC_ID = \"" + NFC_ID+"\"",(error,result,field)=>{
			if(error) throw error;
			
			//if the NFC_ID is not registered, refuse to open the gate
			if(result.length == 0){
				res.json({status:"Failure",info:"The NFC_ID is not registered!"});
				connection.release();
			}
			
			//if balance is greater than 0, release
			//TODO: Update the number of times the user uses the gate
			else if(result[0].Balance > 0){
				connection.query("UPDATE Users SET Balance = " + (result[0].Balance -3) + " WHERE NFC_ID = \""+NFC_ID+"\"",(error,result,field)=>{
					if(error) throw error;
					res.json({status:"Success"});
					connection.release();
				});
			} 
			//else refuse to open the gate
			else{
				res.json({status:"Failure",info:"Balance below zero!"});
				connection.release();				
			}
			
		})
	})

})



app.post("/login",(req,res)=>{
	let username = req.body.username;
	let password = req.body.password;
	pool.getConnection((err,connection)=>{
		if(err) {
			res.json({status:"Failure",info:"Error in database connection"});
			return;
		}
		connection.query("SELECT * FROM Users WHERE Username = \"" + username+"\"",(error,result,field)=>{
			if(error) throw error;
			if(result.length == 0)
				res.json({status:"Failure",info:"User does not exist!"});
			else{
				if(passwordHash.verify(password,result[0].Password))
					res.json({status:"Success",user:username,balance:result[0].Balance});
				else
					res.json({status:"Failure",info:"Password incorrect!"});
			}
			connection.release();
		});

	})

})

app.listen(3000,()=>console.log('Listening on port 3000...'));

