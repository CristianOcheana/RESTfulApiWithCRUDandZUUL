Config:
- there is one user set as admin:
        admin/pass: admin
        
- application starts on port 8080
- pom file contains all needed dependencies
- java 8

How to run:
- start application
- using POSTMAN or similar make requests to http://localhost:8080/products. Use the user to authorize (basic authorization)

GET: possible params: find all / find by id 
example: http://localhost:8080/products  // http://localhost:8080/products/id

POST: http://localhost:8080/products

ID is Auto Generated

body:
{
    "name" : "product1",
    "price" : 201.65,
    "category": "category1"
}

PUT: http://localhost:8080/id
Id is mandatory
partial body:
{
    "price" : 301.65,
}


DELETE: id mandatory parameter
http://localhost:8080/products/id


Application creates HttpServer on port 8080. 
It uses Spring-cloud-zuul for adding a rate limit.
It has basic authorization: user/pass.
RequestHandler class handles the 4 operations: post (create), get (read), put (update), delete (delete)

