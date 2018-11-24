class MyStack {
  int[] vals;
  int top = -1;
  int ERROR_VAL = -13524;

// 11/24/18 myStack is not fully incorporated into this, it may not print correctly, euch
  public MyStack()
  {
    vals = new int[1000];
  }

  public MyStack(int[] vals)
  {
    this.vals = vals;
  }

  public MyStack(int val)
  {
    top++;
    vals = new int[1000];
    vals[top] = val;
    top++;
  }

  boolean push(int val)
  {
    if(top >= 1000)
      return false;

    else
    {
      top++;
      System.out.println("pushing : " +val + " at " + (top));
      vals[top] = val;
    }
    System.out.println(vals[0]);
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

  public void printStack() {
    if(top == 0)
      return;

    System.out.print("[");
    for(int i = 0; i < top; i++) {
      System.out.print(this.vals[i]);
      if(i != top-1)
        System.out.print(", ");
      }
    System.out.println("]");
  }

  public int size() {
    return this.vals.length;
  }
}
