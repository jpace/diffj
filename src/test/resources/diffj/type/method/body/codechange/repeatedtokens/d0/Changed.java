public class Changed {

    public void methodName(String[] ary) {
        String str = null;
        for (int index = 0; index < ary.length; index++) {
            if (str == null || str.length() < 1)
                str = new String(otherMethod(ary[index]));
            else
                str += SEP + otherMethod(ary[index]);
        }
        
        methodName(str);
    }
    
    public String otherMethod(String str) {
        return str;
    }
    
}
