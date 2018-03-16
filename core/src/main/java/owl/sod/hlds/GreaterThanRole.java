 package owl.sod.hlds;
 
 public class GreaterThanRole
   extends CardinalityRole
 {
   public GreaterThanRole() {}
   
   public GreaterThanRole(String name, int cardinality)
   {
     super(name, cardinality);
   }
   
   public boolean equals(Object obj)
   {
     GreaterThanRole gtr;
     if ((obj instanceof GreaterThanRole)) {
       gtr = (GreaterThanRole)obj;
     } else
       return false;
     return (this.name.equals(gtr.name)) && (this.cardinality <= gtr.cardinality);
   }
 }


