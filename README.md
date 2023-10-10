
# Public Library CLI

The main purpose of this project is to build an application who is simulate an automation of some common library processes, which includes register an author, register a book, allow readers to borrow instance of available books and other things that going to be explained better on other sections.


## Documentation

This public library will allow patrons to place books on hold on library. Basically, we will have three kind of patrons:

- Researcher
- Regular
- Student

Each one have some specific constraints you'll see in the next paragraph.

Books will be registered, as well as their respective instances, let's assume the library have available a _"Learn Object Orientation With Java"_ book, and there's two instances registered, the patrons can hold the instance if the instance kind matches the patron permissions. This let us to another explanation, about the book instances types, and the relationship between instance types and patrons types.

The book instances are either restricted or free. Restricted instances can be only held by researchers and students whereas the free instances can be held by anyone. A book hold can be open-ended only for researchers, which means a researcher patron can request a hold without expiration time once per book but for unlimited books, and this kind of requests are only resolved when the patron checks out the book or close the request. A regular patron and students can only have 5 book hold request and they only have access to close-ended book hold, that means the request will expire after some days if the patron does not resolves it.

Any patron with more than two overdue checkouts at a library will get a rejection if trying a hold, and a fee going to be calculated based on the later deadlines. A book can be checked out for up to 60 days and can be extended also on payment of a fee that will be applied only to regular patrons.

How actually a patron knows which books are there to lend? Library has its catalogue of books where books are added together with their specific instances. Each instance must to be associated with the ISBN of the original copy, besides, it is totally possible to have two different instances of the same book.


## Environment Variables

To run this project, you will need to create a `env.properties` file on src/main/resources, and add the following key/values to the file:

`DATABASE_URL`

`DATABASE_USERNAME`

`DATABASE_PASSWORD`

`DATABASE_DRIVER`

To see some example take a look on .env.properties.example file at the same path.


## Run Locally

Clone the project

```bash
  git clone https://github.com/queirozlc/library-cli.git
```

Go to the project directory `src/main/resources` and set up your environment variables.

```bash
  DATABASE_URL=jdbc:oracle:thin:@//localhost:11521/YOUR_DB
  DATABASE_USERNAME=library-root
  DATABASE_PASSWORD=root
  DATABASE_DRIVER=oracle.jdbc.driver.OracleDriver
```

Ensure that you have a JDK installed, the project runs the JAVA 17. Besides, the oracle database must be ready to go.
