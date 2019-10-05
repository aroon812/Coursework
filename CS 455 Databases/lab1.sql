--select customerName, addressLine1, addressLine2, city, state, postalCode from customers
--where salesRepEmployeeNumber IS NULL
--order by customerName

--select customerName, creditLimit from customers 
--where creditLimit > 100000 and creditLimit < 200000
--order by customerName

--select firstName, lastName from employees
--where lastName LIKE 'P%' and firstName LIKE 'M%' or 
--lastName LIKE 'M%' and firstName LIKE 'P%' 
--order by lastName

select productCode from orderdetails NATURAL JOIN
(select orderNumber from customers NATURAL JOIN orders 
where customerName LIKE "Mini Wheels Co." )
 


