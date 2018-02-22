package project.model;

import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
import org.neo4j.driver.v1.types.Relationship;

import java.util.ArrayList;
import java.util.List;

import static org.neo4j.driver.v1.Values.parameters;

public class AccessProvider {
    private Driver driver;

    public AccessProvider(String uri, String user, String password) {
        GraphDatabase database = new GraphDatabase();
        driver = database.driver(uri, AuthTokens.basic(user, password));
    }

    public void close() {
        driver.close();
    }

    /******************************************************************************
     * Dodawanie węzłów
     *****************************************************************************/
    public void addPerson(String name, int year, String status) {
        try (Session session = driver.session()) {
            session.run(
                    "CREATE (n:Person{name: $name, birthyear:$year, status: $status})",
                    parameters("name", name, "year", year, "status", status));
        }
    }

    public void addSubject(String name, int dif) {
        try (Session session = driver.session()) {
            session.run(
                    "CREATE (n:Subject{name: $name, difficulty:$dif })",
                    parameters("name", name, "dif", dif));
        }
    }

    /******************************************************************************
     * Dodawanie Relacji
     *****************************************************************************/
    public void addTeachesRelation(String personName, String subjectName) {
        Node p = findPerson(personName);
        if (p != null & findSubject(subjectName) != null) {
            if (p.get("status").asString().equals("teacher")) {
                try (Session session = driver.session()) {
                    session.run(
                            "MATCH (n:Person{name:$name1}),(s:Subject {name:$name2}) CREATE (n)-[r:TEACHES]->(s)",
                            parameters("name1", personName, "name2", subjectName));
                } catch (Exception e) {
                    System.out.println("Error");
                }
            } else {
                System.out.println("This person isn't a teacher.");
            }
        } else {
            System.out.println("Subject or Person doesn't exist.");
        }
    }

    public void addSupervisedByRelation(String personName1, String personName2) {

        if (findPerson(personName1) != null & findPerson(personName2) != null) {
            try (Session session = driver.session()) {
                session.run(
                        "MATCH (n:Person{name:$name1}),(m:Person {name:$name2}) CREATE (n)-[r:SUPERVISED_BY]->(m)",
                        parameters("name1", personName1, "name2", personName2));
            } catch (Exception e) {
                System.out.println("Error");
            }
        } else {
            System.out.println("One or both of those people don't exist.");
        }
    }

    public void addLearnsRelation(String personName, String subjectName) {
        Node p = findPerson(personName);
        if (p != null & findSubject(subjectName) != null) {
            if (p.get("status").asString().equals("student")) {
                try (Session session = driver.session()) {
                    session.run(
                            "MATCH (n:Person{name:$name1}),(s:Subject {name:$name2}) CREATE (n)-[r:LEARNS]->(s)",
                            parameters("name1", personName, "name2", subjectName));
                } catch (Exception e) {
                    System.out.println("Error");
                }
            } else {
                System.out.println("This person isn't a student.");
            }
        } else {
            System.out.println("Subject or Person doesn't exist.");
        }
    }

    /******************************************************************************
     * Wyszukiwanie węzłów
     *****************************************************************************/
    public Node findNode(String name) {
        try (Session session = driver.session()) {
            StatementResult result = session.run(
                    "MATCH (a) WHERE a.name =$name RETURN a as node",
                    parameters("name", name));
            if (result.hasNext()) {
                Record record = result.next();
                Node n = record.get("node").asNode();
                return n;
            }
        }
        return null;
    }

    public Node findPerson(String name) {
        try (Session session = driver.session()) {
            StatementResult result = session.run(
                    "MATCH (a:Person) WHERE a.name =$name RETURN a as per",
                    parameters("name", name));
            if (result.hasNext()) {
                Record record = result.next();
                Node p = record.get("per").asNode();
                return p;
            }
        }
        return null;
    }

    public Node findSubject(String name) {
        try (Session session = driver.session()) {
            StatementResult result = session.run(
                    "MATCH (a:Subject {name:$name}) RETURN a as sub",
                    parameters("name", name));
            if (result.hasNext()) {
                Record record = result.next();
                // System.out.println(record.get("sub").asNode().get("name").asString());
                return record.get("sub").asNode();
            }
        }
        return null;
    }

    public Node findNodeById(long id) {
        try (Session session = driver.session()) {
            StatementResult result = session.run(
                    "MATCH (a) WHERE ID(a) =$id RETURN a as node",
                    parameters("id", id));
            if (result.hasNext()) {
                Record record = result.next();
                Node p = record.get("node").asNode();

                return p;
            }
        }
        return null;
    }

    /******************************************************************************
     * Szukanie wszystkich relacji węzła
     *****************************************************************************/
    public List<Relationship> findRelationships(String name) {
        List<Relationship> rel = new ArrayList<>();
        try (Session session = driver.session()) {
            StatementResult result = session.run(
                    "MATCH ({name:$name})-[r]-(n) RETURN r as rel",
                    parameters("name", name));
            while (result.hasNext()) {
                Record record = result.next();
                Relationship r = record.get("rel").asRelationship();
                rel.add(r);
            }
            return rel;
        }
    }

    /******************************************************************************
     * Szukanie ścieżki pomiędzy dwoma węzłami
     *****************************************************************************/
    public Path findPath(String name1, String name2) {
        try (Session session = driver.session()) {
            StatementResult result = session.run(
                    "MATCH p=shortestPath((n1 {name:$name1})-[*]-(n2{name:$name2}))RETURN p AS path",
                    parameters("name1", name1, "name2", name2));
            if (result.hasNext()) {
                Record record = result.next();
                Path p = record.get("path").asPath();
                System.out.println(p.toString());
                return p;
            }
        }
        return null;
    }

    /******************************************************************************
     * Wypisywanie
     *****************************************************************************/
    public void printNode(Node n) {
        String label;
        if (n.labels().iterator().hasNext()) {
            label = n.labels().iterator().next();
            System.out.println("Name: " + n.get("name"));
            System.out.println("Label: " + n.labels());
            System.out.println("ID: " + n.id());

            if (label.equals("Person")) {
                System.out.println("Year of birth: " + n.get("birthyear"));
                System.out.println("Status: " + n.get("status"));
                if (n.get("status").equals("student")) {
                    System.out.println("Grade: " + n.get("grade"));
                }
            } else if (label.equals("Subject")) {
                System.out.println("Difficulty: " + n.get("difficulty"));
            }
            System.out.println("");
        }
    }

    public void printRelationship(Relationship r){
        Node start = findNodeById(r.startNodeId());
        Node end = findNodeById(r.endNodeId());
        System.out.println("(" + start.get("name") + ")-[" + r.type() + "]->(" +end.get("name") + ")");
    //    System.out.println(r);
    }

    public void printPath(Path path){
        while (path.relationships().iterator().hasNext()){
            Relationship r = path.relationships().iterator().next();
            printRelationship(r);
        }
    }

    /*****************************************************************************/
    public void printPeopleByInitial(String initial) {
        try (Session session = driver.session()) {
            // Auto-commit transactions are a quick and easy way to wrap a read.
            StatementResult result = session.run(
                    "MATCH (a:Person) WHERE a.name STARTS WITH {x} RETURN a.name AS name",
                    parameters("x", initial));
            // Each Cypher execution returns a stream of records.
            while (result.hasNext()) {
                Record record = result.next();
                // Values can be extracted from a record by index or name.
                System.out.println(record.get("name").asString());
            }
        }
    }

    public void printAllPeople() {
        try (Session session = driver.session()) {
            StatementResult result = session.run(
                    "MATCH (a:Person) RETURN a.name AS name");
            while (result.hasNext()) {
                Record record = result.next();
                System.out.println(record.get("name").asString());
            }
        }
    }

    public void printByStatus(String status) {
        try (Session session = driver.session()) {
            // Auto-commit transactions are a quick and easy way to wrap a read.
            StatementResult result = session.run(
                    "MATCH (a:Person) WHERE a.status = status RETURN a.name AS name",
                    parameters("status", status));
            while (result.hasNext()) {
                Record record = result.next();
                System.out.println(record.get("name").asString());
            }
        }
    }
}