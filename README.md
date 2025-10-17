# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

<details>
    [![Sequence Diagram](./sequence_diagram.svg)](https://sequencediagram.org/index.html#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKpgtIiopAC0AHzklDRQAFwwANoACgDyZAAqALowAPQ+BlAAOmgA3gBE3ZRowAC2KEOVQzBDADTzuOoA7tAcM3OL8ygTwEgIW-MAvpjCFTAlrOxclNXDo1DjU8c7Qyuq61Cbs-NLQz2ByOfyGZzYnG4sGuF1E1SgmWyuSgAAoMlkcpQMgBHVI5ACU53KoiupVk8iUKnU1XsKDAAFUeiini8UITyYplGpVKSjDpKgAxJCcGCMygcmA6SwwFmTMQ6BHAADWop6MFWSDA8RlPVZMGACEVHGlKAAHkiNBzKdyrjDiSpqmKoByiSIVLbShd7jAFIaUMBjc1legAKKmlTYAgQNCuy6Fa75czVQJOYL9YZTdTAWkzeYhqDeKo6sZy-V+gPS+RK9BgsycTBWrnqD1lN0oapoHwIBCxknXRtU1SVECK3JO5k9Dns7TW5vXYyVBQcDiq8XaXvu-szptDkf+3IKHxalHAI-xKcN7eD3kLpcrw9al2wzfXCF3IvopFYtRdrBvqEtl6RaPLqcpzNU2wAqeWrNBA1ZoOB8ynLGlAtomGDVOEThOOmIyga8MAQf88zQfEsHwYh2xnOgHCmF4vgBNA7C0jEApwCG0hwAoMAADIQFkMbocwdrUN69RNG0nQGOoSDRrhsqvKCAKfN8vxUShUAlP+9wgSWimQcs+hfBsbzgrcUIlM+7YIPxwoonxAk4niYCElZJQDtylS0gyTIKWyl4UjuJQLkKIpOhKUrFs8cobigAA88YedS4Xrm5xRWZUFRIAAZpYTp1EZqkoil8hLCpGyufaKDuVenl7nIKAPvEJ5nheSWqMF-J3j6Z4yOu2lQAlhQZQ5woZKov4aUNA2VAM8x+W8UFnuRNagjAZxCUNQGVFhOGDPN+HTEpJHLXBq3bOtda0Z43h+P4XgoOgMRxIkD1PQ5vhYEJvLbbU0ghjxIbNCG7QdNJqiyWg-SkStMZAVp5n3KR0BIAAXnIkPVHFMNnWgxSYANllVZUNn2J99n8Z9TlqC5sU1YFg5eXS444-B04M9ynWVAA4nSq6wNl3gTD1WrmIQGPRjAsOxUN7WVCVwB08UcsIhMEA0E1E5rvI7OcoOXMZGrND6meYsgBLaAwJA8H6uoEBEA1K4alq-ME4jg3xiNlNHuNk1AdN7uzRtyDmFt5T3Lt6bUfWN0Mf4CIrv42DCiqPFIjA3Nyho30iRU1Q1NzQOg-YcrQ6d8EaQjkJI2eKPo1GaBY6z6D44T6XE8gOSZ5m9lIt3ajUwSSty95LPl+guuzh1xQLrzzBOjAgsQMLTVmxbUu4zLiW1clk6pVV9N6553m+gg-eqCik9BTP-Jzz63YZ1nW+FHLcAQN2KDgJDcX9xyrfu2HNslQ05dyzuNbA0YDBTXjDNOaQwS6ZlzDUYYCCUAAElpC5gAIzhGCIEAEqx4iahQE6Vki15jJFAEqUhYFjrwLlAAOTlKCE4SwXDsJgK0YOBQwCALzpHfa9DEH5xQXKDB2DcH4PmIQ4hND9LESGJQkA1DDrkKESgJhUwWFsI4Vwq6dFboBA4AAdjcE4FATgYghmCHAdiAA2eAo5DD9xgJgHOnpw5FnEi0YupdxjjyhoI1BmijoXTOPDYoM1kZQDRhbJuATijpnmME2hYS3bV00u3IB9Vcj9xRHAJx-dB60zSiPZmTJm5oCvvrG+PM+YLyXivU2GBxYNw3hXKyssd5DgVsPbpw5Clyi1s6bQ1TOa1O6mgFAqxH5THVJqbUC8nxVS6Rzakv9+oAM9sTAp+4UBFLUBAtAUD-YwMDnA1B4jqg4LwZdTa2zRJVAERcsRmDrmSMujRAxcdLAoG7BAGZsQkAJDAL8-5MyABSEBhSzMMP4JRSo3EhyKB4x5+c6j0kkh0VBZcYK4ySR8BAwBflQDfjZKAWwADqLA0HAw6AAIR4goOAABpd4ly3kwBuYES6ESom1xifXTGMBsYJIJdgIlJKyXQCpTSuljLmVsoBByiRtyzIZKJkAgAVtCtAeSoXCgObiGmlU2yHynkzHylAWp4rZgFI+6guZ3waULEW8Q15tOlp07eayel73kH031lq8kcrGY62pd8XGNNhTADBW85YbIDaU-pAYOCwTyU8DkZVJWUGlVAMN08Fz0mwFoXIMbnbaimasBA0pU0oBXAvCtVt3VIFgBK4lqEQDv2gOk98fC4QGr1XKcBkCxCnMKLA7hocHn8OwlHfRsc7peGJc9YFr0oArsQP6WAwBsAStaZDVx7jWx5z+gDIGINOjGErpE856r3yaqoHCEA3A8AKD3cgc2DdL6BodbuV9UBT5OgvgWrm0g-l0jUGWBA-MeQwtFkYbQegTkH2Vv0l927T5NVhiB+1U8uZbrwDyA0MHV4tK-YenDMB4PusQ-IZDY7UNyww2+7s58f3tTAxB3IxGH7n2o5bBDpbdD6DEANftDpCOAY-Qe6MI7jmMceQHDJQckU8L4RHOdgxo60SAA)
</details>

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](./starter-code) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
|----------------------------| ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
