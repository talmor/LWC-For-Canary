package com.griefcraft.util;

public class StringUtils
{
  public static String capitalizeFirstLetter(String paramString)
  {
    if (paramString.length() == 0)
      return paramString;
    if (paramString.length() == 1)
      return paramString.toUpperCase();
    String str1 = paramString.substring(0, 1);
    String str2 = paramString.substring(1);
    return str1.toUpperCase() + str2.toLowerCase();
  }

  public static boolean hasFlag(String[] paramArrayOfString, String paramString)
  {
    String str = paramArrayOfString[0].toLowerCase();
    return (str.equals(paramString)) || (str.equals("-" + paramString));
  }

  public static String join(String[] paramArrayOfString, int paramInt)
  {
    String str = "";
    if ((paramArrayOfString == null) || (paramArrayOfString.length == 0))
      return str;
    for (int i = paramInt; i < paramArrayOfString.length; i++)
      str = str + paramArrayOfString[i] + " ";
    return str.trim();
  }

  public static String transform(String paramString, char paramChar)
  {
    char[] arrayOfChar = paramString.toCharArray();
    for (int i = 0; i < arrayOfChar.length; i++)
      arrayOfChar[i] = paramChar;
    return new String(arrayOfChar);
  }
}

/* Location:           D:\dev\Minecraft Mods\server_1.6.Crow_b1.1.7\plugins\LWC.jar
 * Qualified Name:     com.griefcraft.util.StringUtils
 * JD-Core Version:    0.6.0
 */