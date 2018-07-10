class MyStack
{
  int[] vals;
  int top;
  
  MyStack()
  {
  }
  
  MyStack(int[] vals)
  {
    this.vals = vals;
  }
  
  MyStack(int val)
  {
    top = 0;
    vals = new int[1000];
    val[top] = val;
    top++;
  }
  
  push(int val)
  {
    if(top >= 1000)
      return;
      
    else
    {
      vals[top] = val;
      top++;
    }
  }
  
  pop()
  {
    top--;
    return vals[top+1];
  }
  
  peek()
  {
    return vals[top];
  }
  

}
