 package owl.sod.hlds;
 
 import java.util.Vector;


 public class Composition
 {
   public Vector<Item> Rc;
   public Item Du;
   
   public Composition(Vector<Item> Rc, Item Du)
   {
     this.Rc = Rc;
     this.Du = Du;
   }
 }


