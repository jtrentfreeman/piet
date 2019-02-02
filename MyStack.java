class MyStack {
  int[] vals;
  int top = -1;

  int STACK_LIMIT = (int) Math.pow(2, 16);
  int ERROR_VAL = Integer.MIN_VALUE;

// 11/24/18 myStack is not fully incorporated into this, it may not print correctly, euch
  public MyStack()
  {
    vals = new int[STACK_LIMIT];
  }

  public MyStack(int[] vals)
  {
    this.vals = vals;
  }

  public MyStack(int val)
  {
    top++;
    vals = new int[STACK_LIMIT];
    vals[top] = val;
    top++;
  }

  boolean push(int val)
  {
    System.out.println("PUSHING " + val);
    if(top >= STACK_LIMIT) {
      System.out.println("REACHED TOP OF STACK");
      return false;
    }

    else
    {
      top++;
      System.out.println("pushing : " +val + " at " + (top));
      vals[top] = val;
    }
    this.printStack();
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
    System.out.println("PRINTING STACK");
    if(top == 0) {
      System.out.println("[]");
      return;
    }

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
