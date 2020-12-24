# Whispering Pines Gate receiver testing

In order to run this application you will need:

1) Java (JDK higher than 1.8)
2) Maven (preferribly 3.6 or higher)
3) Git

If you haven't ever using Java or maven before, check out
   https://www.oracle.com/webfolder/technetwork/tutorials/obe/java/Maven_SE/Maven.html

Checkout out the code from GitHub
   `git clone https://github.com/bsmichael/gate.git`

Change to the `gate` directory
   `cd gate`

Run the application with
   `mvn clean package exec:java -Dexec.mainClass="com.wp.App"`
