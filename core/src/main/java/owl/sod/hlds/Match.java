 package owl.sod.hlds;
 
 
 public class Match
 {
   public Abduction abduction;
   
   public Contraction contraction;
   
   public double score;
   
 
   public Match()
   {
     this.abduction = new Abduction();
     this.contraction = new Contraction();
     this.score = 0.0D;
   }
   
   public Match(Abduction abductionResult, Contraction contractionResult) {
     this.abduction = abductionResult;
     this.contraction = contractionResult;
     computeScore();
   }
   
 
   public void computeScore() {}
   
   public void setAbduction(Abduction abductionResult)
   {
     this.abduction = abductionResult;
   }
   
   public void setContraction(Contraction contractionResult) {
     this.contraction = contractionResult;
   }
 }


