package beka.com.bk.dushanbeonline.custom_views;

import java.util.ArrayList;
import java.util.List;

public class Tree {
    Object data;
    ArrayList<Tree> children = new ArrayList<Tree>();
    public Tree(Object data){
        this.data = data;
    }

    public void addChildren(Tree child){
        children.add(child);
    }

    public Object getData(){
        return data;
    }

    public Tree getChild(int n){
        if(children.size()>n) {
            return children.get(n);
        }else{
            return null;
        }
    }
}
