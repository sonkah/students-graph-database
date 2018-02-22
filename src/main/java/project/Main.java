package project;

import org.neo4j.driver.v1.types.Path;
import org.neo4j.driver.v1.types.Relationship;
import project.model.AccessProvider;

import java.util.List;

public class Main{

    private final static String uri = "bolt://localhost:7687", passwd = "password", user = "neo4j";

    public static void main(String args[]){
        AccessProvider provider = new AccessProvider(uri, user, passwd);

        List<Relationship> r = provider.findRelationships("History");
        for(Relationship rel : r){
            provider.printRelationship(rel);
        }
        
        if(provider.findPath("Mary Sue", "Peter Parker") != null) {
            Path p = provider.findPath("Mary Sue", "Peter Parker");
            //    provider.printPath(p);
        }



        //dataPropagator(provider);
//        System.out.println(provider.findNode("Albus Dumbledore").get("status"));
//        System.out.println(provider.findNode("Albus Dumbledore").get("status").asString().equals("teacher"));
        //provider.printPeopleByInitial("M");
        //provider.printNode(provider.findSubject("History"));
        //provider.printNode(provider.findNodeById(0));
        //System.out.println(provider.findNodeById(0) == null);
      //provider.printPeopleByInitial("A");

        //provider.findRelationships("History");

        //provider.findPath("History", "Chemistry");
        provider.close();
    }

    public static void dataPropagator(AccessProvider provider){
        provider.addSubject("Geography", 5);
        provider.addSubject("Physics", 8);
        provider.addSubject("Math", 7);
        provider.addSubject("Chemistry", 7);
        provider.addSubject("History", 4);

        provider.addPerson("Henry Ford", 2001, "student");
        provider.addPerson("Harry Potter", 2001, "student");
        provider.addPerson("Peter Parker", 2001, "student");
        provider.addPerson("Hermiona Granger", 2001, "student");
        provider.addPerson("James Hawkins", 2001, "student");
        provider.addPerson("Jane Doe", 2000, "student");
        provider.addPerson("Mary Sue", 1999, "student");

        provider.addPerson("Gall Anonim", 2001, "teacher");
        provider.addPerson("Albus Dumbledore", 2001, "teacher");
        provider.addPerson("Marie Curie", 2001, "teacher");
        provider.addPerson("Albert Einstein", 2001, "teacher");
        provider.addPerson("Phileas Fogg", 2001, "teacher");
        provider.addPerson("Walter White", 2001, "teacher");

        provider.addTeachesRelation("Walter White", "Chemistry");
        provider.addTeachesRelation("Marie Curie", "Chemistry");
        provider.addTeachesRelation("Marie Curie", "Physics");
        provider.addTeachesRelation("Albert Einstein", "Physics");
        provider.addTeachesRelation("Albert Einstein", "Math");
        provider.addTeachesRelation("Phileas Fogg", "Geography");
        provider.addTeachesRelation("Gall Anonim", "History");
        provider.addTeachesRelation("Albus Dumbledore", "History");

        provider.addLearnsRelation("Harry Potter", "Chemistry");
        provider.addLearnsRelation("Harry Potter", "History");
        provider.addLearnsRelation("Henry Ford", "Chemistry");
        provider.addLearnsRelation("Hermiona Granger", "History");
        provider.addLearnsRelation("James Hawkins", "Geography");
        provider.addLearnsRelation("Jane Doe", "Math");
        provider.addLearnsRelation("Jane Doe", "Physics");
        provider.addLearnsRelation("Jane Doe", "Geography");
        provider.addLearnsRelation("Mary Sue", "Physics");
        provider.addLearnsRelation("Peter Parker", "Math");
        provider.addLearnsRelation("Henry Ford", "Math");

        provider.addSupervisedByRelation("Walter White", "Marie Curie");
        provider.addSupervisedByRelation("Marie Curie","Albus Dumbledore");
        provider.addSupervisedByRelation("Gall Anonim","Albus Dumbledore");
        provider.addSupervisedByRelation("Albert Einstein","Albus Dumbledore");
        provider.addSupervisedByRelation("Phileas Fogg","Albus Dumbledore");
        provider.addSupervisedByRelation("Walter White","Albus Dumbledore" );

    }

}