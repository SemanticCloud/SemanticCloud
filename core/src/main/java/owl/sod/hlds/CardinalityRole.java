 package owl.sod.hlds;
 
 public class CardinalityRole extends Concept {
   public int cardinality;
   
   public CardinalityRole() {
     super("");
   }
   
   public CardinalityRole(String name, int cardinality) {
     super(name);
     this.cardinality = cardinality;
   }
   
   public boolean isTheSame(CardinalityRole role) {
     return (this.name.equals(role.name)) && (this.cardinality == role.cardinality);
   }
 }


