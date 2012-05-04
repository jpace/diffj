public class Changed {
    
    public void methodName(String[] ary) {
        StringBuilder as = null;

        for (int index = 0; index < ary.length; index++) {
            if (index == 0) {
                as = new StringBuilder(otherMethod(ary[index]));
            }
            else {
                as.append(SEP).append(otherMethod(ary[index]));
            }
        }        
        methodName(as.toString());
    }    

    public String otherMethod(String str) {
        return str;
    }

}
