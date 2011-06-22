public abstract interface LWC_Command
{
  public abstract void execute(LWC paramLWC, Player paramPlayer, String[] paramArrayOfString);

  public abstract boolean validate(LWC paramLWC, Player paramPlayer, String[] paramArrayOfString);
}
