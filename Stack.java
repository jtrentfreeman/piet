class MyStack
{
  int[] vals;
  int top = -1;
  int ERROR_VAL = -13524;
  
  MyStack()
  {
    vals = new int[1000];
  }
  
  MyStack(int[] vals)
  {
    this.vals = vals;
  }
  
  MyStack(int val)
  {
    top++;
    vals = new int[1000];
    val[top] = val;
    top++;
  }
  
  boolean push(int val)
  {
    if(top >= 1000)
      return false;
      
    else
    {
      vals[top] = val;
      top++;
    }
    
    return true;
  }
  
  int pop()
  {
    if(top == -1)
      return ERROR_VAL;
    
    top--;
    return vals[top+1];
  }
  
  int peek()
  {
    return vals[top];
  }
  

}
