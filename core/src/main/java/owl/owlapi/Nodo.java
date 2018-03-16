 package owl.owlapi;
 
 import java.util.ArrayList;

 public class Nodo
 {
   private int name;
   private ArrayList<Nodo> predecessors;
   private ArrayList<Nodo> successors;
   private ArrayList<Nodo> equivalents;
   
   public Nodo(int nome) {
     this.name = nome;
     this.predecessors = new ArrayList();
     this.successors = new ArrayList();
     this.equivalents = new ArrayList();
   }
   
   public void setName(int name) {
     this.name = name;
   }
   
   public int getName() {
     return this.name;
   }
   
   public ArrayList<Nodo> getPredecessors() {
     return this.predecessors;
   }
   
   public ArrayList<Nodo> getSuccessors() {
     return this.successors;
   }
   
   public ArrayList<Nodo> getEquivalents() {
     return this.equivalents;
   }
   
   public boolean equals(Nodo node) {
     return getName() == node.getName();
   }
 }


