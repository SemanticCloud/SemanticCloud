 package owl.sod.hlds;
 
 public class LessThanRole
   extends CardinalityRole
 {
   public LessThanRole() {}
   
   public LessThanRole(String name, int cardinality)
   {
     super(name, cardinality);
   }
   
   public boolean equals(Object obj)
   {
     LessThanRole ltr;
     if ((obj instanceof LessThanRole)) {
       ltr = (LessThanRole)obj;
     } else
       return false;
     return (this.name.equals(ltr.name)) && (this.cardinality >= ltr.cardinality);
   }
 }


