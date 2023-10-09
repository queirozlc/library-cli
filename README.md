
# Public Library CLI

The main purpose of this project is to build an application who is simulate an automation of some common library processes, which includes register an author, register a book, allow readers to borrow instance of available books and other things that going to be explained better on other sections.


## Documentation

[Documentation](https://linktodocumentation)


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
```

Ensure that you have a JDK installed, the project runs the JAVA 17. Besides, the oracle database must be ready to go.
