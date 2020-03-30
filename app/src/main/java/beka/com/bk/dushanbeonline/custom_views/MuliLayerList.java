package beka.com.bk.dushanbeonline.custom_views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import beka.com.bk.dushanbeonline.ResourseColors;

public class MuliLayerList extends FrameLayout {
    ListCategoryView view;
    Tree allCategories;
    ArrayList<ListCategoryView> categoryViews = new ArrayList<>();
    int lang = 0;
    float scale = 1;
    int width, height;
    TopLabel label;
    LoadCircle loading;


    public MuliLayerList(@NonNull Context context, int width, int height, int lan) {
        super(context);
        scale = context.getResources().getDisplayMetrics().widthPixels/1080f;
        this.lang = lan;
        this.width = width;
        this.height = height;
        setLayoutParams(new FrameLayout.LayoutParams(width,height));
        System.out.print("start");

        loading = new LoadCircle(context);
        loading.setY(300*scale);
        loading.setX(1080/2*scale-loading.bmp.getWidth()/2);
        addView(loading);
        loading.start();

        allCategories = new Tree("root");

        FirebaseDatabase.getInstance().getReference("cvalifications").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                MyTask task = new MyTask();
                task.execute(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loading.stop();
            }
        });

    }
    public ListCategoryView all= new ListCategoryView(getContext(),(String)(""));
    private void buildListView() {
        //tree ready build view
        all.opened = true;

        System.out.println("abab "+allCategories.children.size());
        for(int i =0; i<allCategories.children.size();i++){
            ListCategoryView categ = new ListCategoryView(getContext(),(String) (allCategories.children.get(i).data));
            all.addChild(categ,false);
            categ.offset = 50*scale;
            for(int j=0;j<allCategories.children.get(i).children.size();j++){
                if((j>=5)&(i==0)) {
                    ListCategoryView subCateg = new ListCategoryView(getContext(), (String) (allCategories.children.get(i).children.get(j).data));
                    categ.addChild(subCateg, true);
                    subCateg.offset = 150 * scale;
                    for (int k = 0; k < allCategories.children.get(i).children.get(j-5).children.size(); k++) {
                        ListCategoryView subSubCateg = new ListCategoryView(getContext(), (String) (allCategories.children.get(i).children.get(j-5).children.get(k).data));
                        subSubCateg.offset = 250 * scale;
                        subCateg.addChild(subSubCateg, true);
                    }
                }else{
                    if(i!=0){
                        ListCategoryView subCateg = new ListCategoryView(getContext(), (String) (allCategories.children.get(i).children.get(j).data));
                        categ.addChild(subCateg, true);
                        subCateg.offset = 150 * scale;
                        for (int k = 0; k < allCategories.children.get(i).children.get(j).children.size(); k++) {
                            ListCategoryView subSubCateg = new ListCategoryView(getContext(), (String) (allCategories.children.get(i).children.get(j).children.get(k).data));
                            subSubCateg.offset = 250 * scale;
                            subCateg.addChild(subSubCateg, true);
                        }
                    }
                }
            }
            categ.setY(i*122*scale);
            //addView(categ);
            //categ.invalidate();
            categoryViews.add(categ);
        }
    }

    class MyTask extends AsyncTask<DataSnapshot, Void, Void> {

        @Override
        protected Void doInBackground(DataSnapshot... dataSnapshots) {
            if(dataSnapshots[0]!=null){

                ArrayList<Object> general = ((ArrayList<Object>)dataSnapshots[0].getValue());

                for(Object subCategory : general){
                    if(subCategory instanceof Map){
                        if(((Map<Object,Object>)subCategory).get("AmountOfSubs")!=null){
                            Tree treeSub = new Tree("null");
                            if(lang == 0) {
                                treeSub = new Tree((String) ((Map<Object, Object>) subCategory).get("engName"));
                            }
                            if(lang == 1) {
                                treeSub = new Tree((String) ((Map<Object, Object>) subCategory).get("rusName"));
                            }

                            Long subs =(Long) ((Map<Object,Object>)subCategory).get("AmountOfSubs");

                            for(int i = 0; i<subs;i++){
                                Map<Object,Object> subSubCategories = (Map<Object,Object>)((Map<Object,Object>)subCategory).get(""+i);

                                Tree treeSubSub = new Tree("null");
                                if(lang == 0) {
                                    treeSubSub = new Tree(subSubCategories.get("engName"));
                                }
                                if(lang == 1) {
                                    treeSubSub = new Tree(subSubCategories.get("rusName"));
                                }

                                Long sububs =(Long) (subSubCategories).get("amountOfSubs");
                                if(sububs==null){
                                    sububs=0l;
                                }

                                for(int j = 0; j<sububs;j++){
                                    if(lang == 0) {
                                        treeSubSub.addChildren(new Tree(subSubCategories.get("engSub" + j)));
                                    }
                                    if(lang == 1)
                                        treeSubSub.addChildren(new Tree(subSubCategories.get("rusSub" + j)));
                                }
                                treeSub.addChildren(treeSubSub);
                            }
                            allCategories.addChildren(treeSub);
                        }

                    }
                }

            }else{
                System.out.print("abab "+"datasnapshot == null");
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }



        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            buildListView();
            for(int i =0;i<categoryViews.size();i++){
                addView(categoryViews.get(i));
            }
            loading.stop();
            checkCategories();
        }
    }

    private void checkCategories(){
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            FirebaseDatabase.getInstance().getReference("profiInfo").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String defCateg = "";
                    if(dataSnapshot.child("defaultCategory")!=null){
                        defCateg = dataSnapshot.child("defaultCategory").getValue(String.class);
                    }
                    if(dataSnapshot.child("categories")!=null){
                        if(dataSnapshot.child("categories").getValue() !=null) {

                            if (dataSnapshot.child("categories").getValue() instanceof ArrayList) {
                                ArrayList<String> categories = (ArrayList<String>) dataSnapshot.child("categories").getValue();
                                for (int i = 0; i < categories.size(); i++) {
                                   // System.out.println("abab" + categories.get(i).split("-", 3)[0] + " " + categories.get(i).split("-", 3)[1] + " " + categories.get(i).split("-", 3)[2]);
                                    String[] categs = categories.get(i).split("-", 3);
                                    int k = 0;
                                    ListCategoryView target = all;
                                    while (k < categs.length) {
                                        if (!categs[k].equals("*"))
                                            target = target.childs.get(Integer.parseInt(categs[k]));
                                        k++;
                                    }
                                    target.isChecked = true;
                                    target.isPayed = true;
                                    if(defCateg.equals(categories.get(i))){
                                        target.isDefault = true;
                                    }
                                    target.invalidate();
                                }
                            } else {
                                System.out.println("ababb not array " + dataSnapshot.getValue().getClass().getName());
                            }
                        }

                    }else{
                        System.out.println("ababb null");
                    }
                    all.reflect();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }


}