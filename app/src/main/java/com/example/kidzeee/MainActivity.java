package com.example.kidzeee;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crowdfire.cfalertdialog.CFAlertDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ImageButton MainNextButton ;
    private List<KidzeeModel> KidzeeList;
    private static int CurrentItem=0;
    private TextToSpeech MainTextToSpeech;
    private RecyclerView SingleItemCorrectRecyclerView,SingleItemJumbledRecyclerView;
    private CorrectAdapter correctAdapter;
    private Toast WrongToast;
    private View WrongView;
    private SingleKidzeeAdapter singleKidzeeAdapter;
    private ImageView SingleItemImageView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        MainNextButton = findViewById(R.id.main_next_button);
        SingleItemImageView = findViewById(R.id.single_item_imageview);

        SingleItemJumbledRecyclerView = findViewById(R.id.single_item_jumbled_reyclerview);
        SingleItemJumbledRecyclerView.setHasFixedSize(true);
        SingleItemJumbledRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));

        SingleItemCorrectRecyclerView = findViewById(R.id.single_item_correct_reyclerview);
        SingleItemCorrectRecyclerView.setHasFixedSize(true);
        SingleItemCorrectRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));

        KidzeeList = new ArrayList<>();

        CurrentItem=0;
        animalList();
        LoadFirstItem();

        MainNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(CurrentItem < KidzeeList.size())
                {
                    try
                    {
                        findViewById(R.id.tick).setVisibility(View.INVISIBLE);
                        findViewById(R.id.tick_card).setVisibility(View.INVISIBLE);

                        CurrentItem++;
                        Glide.with(getApplicationContext()).load(KidzeeList.get(CurrentItem).getImageUri()).into(SingleItemImageView);

                        singleKidzeeAdapter = new SingleKidzeeAdapter(KidzeeList.get(CurrentItem).getOrgName(),GenerateRandomString(KidzeeList.get(CurrentItem).getOrgName()));
                        singleKidzeeAdapter.notifyDataSetChanged();
                        SingleItemJumbledRecyclerView.setAdapter(singleKidzeeAdapter);

                        correctAdapter= new CorrectAdapter(KidzeeList.get(CurrentItem).getOrgName(),0);
                        correctAdapter.notifyDataSetChanged();
                        SingleItemCorrectRecyclerView.setAdapter(correctAdapter);
                    }
                    catch (IndexOutOfBoundsException e)
                    {
                        Toast.makeText(getApplicationContext(),"Last item reached",Toast.LENGTH_SHORT).show();
                    }


                }

            }
        });

        MainTextToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS)
                {
                    int result = MainTextToSpeech.setLanguage(Locale.US);

                    if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
                    {
                        Toast.makeText(getApplicationContext(),"Language not supported",Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"TextToSpeech initialization failed.",Toast.LENGTH_SHORT).show();

                }
            }
        });


        WrongToast = new Toast(this);
        WrongView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.wrong_view_layout,null);
        WrongToast.setView(WrongView);
        WrongToast.setGravity(Gravity.CENTER,0,0);

       findViewById(R.id.selectItem).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
                 showDialogue();
           }
       });

         if(isConnectedToNet()){

         }else{
             CFAlertDialog.Builder builder = new CFAlertDialog.Builder(MainActivity.this)
                     .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                     .setTitle("No internet ")
                     .setMessage("No internet. Please turn it ON")
                     .setDialogBackgroundColor(MainActivity.this.getResources().getColor(R.color.cfdialog_button_white_text_color))
                     .setIcon(R.drawable.ic_cancel_black_24dp)
                     .addButton("  CANCEL ", -1, Color.RED,
                             CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {
                             dialog.dismiss();
                         }
                     });

             builder.show();


         }




    }

    private void LoadFirstItem() {


           findViewById(R.id.tick).setVisibility(View.INVISIBLE);
           findViewById(R.id.tick_card).setVisibility(View.INVISIBLE);
           Glide.with(getApplicationContext()).load(KidzeeList.get(CurrentItem).getImageUri()).into(SingleItemImageView);

           singleKidzeeAdapter = new SingleKidzeeAdapter(KidzeeList.get(CurrentItem).getOrgName(),GenerateRandomString(KidzeeList.get(CurrentItem).getOrgName()));
           singleKidzeeAdapter.notifyDataSetChanged();
           SingleItemJumbledRecyclerView.setAdapter(singleKidzeeAdapter);

           correctAdapter= new CorrectAdapter(KidzeeList.get(CurrentItem).getOrgName(),0);
           correctAdapter.notifyDataSetChanged();
           SingleItemCorrectRecyclerView.setAdapter(correctAdapter);
    }

    private void fruitList() {
        CurrentItem=0;
        KidzeeList.clear();
        KidzeeList.add(new KidzeeModel("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSYP3GJ7QuLRvmuh-agl1sXHtMZ1VQwWKwxADnICJDaeiYPUqv_","MANGO"));
        KidzeeList.add(new KidzeeModel("https://5.imimg.com/data5/WA/US/MY-18632401/apple-fruit-500x500.jpg","APPLE"));
        KidzeeList.add(new KidzeeModel("https://images-na.ssl-images-amazon.com/images/I/81WJyO53YAL._SY550_.jpg","PINEAPPLE"));
        KidzeeList.add(new KidzeeModel("https://previews.123rf.com/images/utima/utima1202/utima120200025/12521354-wet-orange-fruit-with-leaves-isolated-on-white.jpg","ORANGE"));
        KidzeeList.add(new KidzeeModel("https://i5.walmartimages.ca/images/Large/463/3_r/6000191284633_R.jpg","WATERMELON"));
        KidzeeList.add(new KidzeeModel("https://ae.pricenacdn.com/files/images/products/original/933/Banana-Yellow-India-1kg-Approx-Weight_11074279_032b1d60acc6d81ec479d6a6dd68b825_t.jpg","BANANA"));
        KidzeeList.add(new KidzeeModel("https://cdn.medusajuice.co.uk/wp-content/uploads/2017/12/peach-e-liquid.png","PEACH"));
        KidzeeList.add(new KidzeeModel("http://www.zarat.kp.gov.pk/assets/uploads/crops/a2237670547780096024583f333bcefd.jpeg","CARROT"));
        KidzeeList.add(new KidzeeModel("https://balidirectstore.com/app/uploads/2018/04/Fresh-lemons-on-the-rustic-tale-640x360.jpg","LEMON"));
        LoadFirstItem();
    }


    private void animalList(){
        KidzeeList.clear();
        CurrentItem=0;
        KidzeeList.add(new KidzeeModel("https://upload.wikimedia.org/wikipedia/commons/7/73/Lion_waiting_in_Namibia.jpg", "LION"));
        KidzeeList.add(new KidzeeModel("https://upload.wikimedia.org/wikipedia/commons/8/81/2012_Suedchinesischer_Tiger.JPG","TIGER"));
        KidzeeList.add(new KidzeeModel("https://upload.wikimedia.org/wikipedia/commons/b/b2/Hausziege_04.jpg","GOAT"));
        KidzeeList.add(new KidzeeModel("https://upload.wikimedia.org/wikipedia/commons/thumb/0/0c/Cow_female_black_white.jpg/1200px-Cow_female_black_white.jpg","COW"));
        KidzeeList.add(new KidzeeModel("https://upload.wikimedia.org/wikipedia/commons/1/11/Cheetah_Kruger.jpg","CHEETAH"));
        KidzeeList.add(new KidzeeModel("https://upload.wikimedia.org/wikipedia/commons/0/07/Giraffe08_-_melbourne_zoo.jpg","GIRAFFE"));
        KidzeeList.add(new KidzeeModel("https://upload.wikimedia.org/wikipedia/commons/b/b7/White-tailed_deer.jpg","DEER"));
        KidzeeList.add(new KidzeeModel("https://upload.wikimedia.org/wikipedia/commons/4/4e/Macaca_nigra_self-portrait_large.jpg","MONKEY"));
        KidzeeList.add(new KidzeeModel("https://upload.wikimedia.org/wikipedia/commons/e/ed/Water-buffalo.jpg","BUFFALO"));
        LoadFirstItem();
    }

    public boolean isConnectedToNet() {

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;


    }

    private String GenerateRandomString(String orgName) {

        int length = orgName.length();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rnd = new Random();

        while(orgName.length()<10)
        {
            char c = chars.charAt(rnd.nextInt(chars.length()));

            if(!orgName.contains(String.valueOf(c)))
            {
                orgName=orgName+String.valueOf(c);
            }
        }


        return shuffle(orgName);
    }

    public String shuffle(String input){
        List<Character> characters = new ArrayList<Character>();
        for(char c:input.toCharArray()){
            characters.add(c);
        }
        StringBuilder output = new StringBuilder(input.length());
        while(characters.size()!=0){
            int randPicker = (int)(Math.random()*characters.size());
            output.append(characters.remove(randPicker));
        }

        return output.toString();
    }

    public class SingleKidzeeAdapter extends RecyclerView.Adapter<SingleKidzeeAdapter.SingleKidzeeViewholder>
    {
        String OrgString;
        String RandomString;
        int count;

        public SingleKidzeeAdapter(String orgString, String o) {

            OrgString = orgString;
            RandomString= o;
            count=0;
        }

        @NonNull
        @Override
        public SingleKidzeeViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
            return new SingleKidzeeAdapter.SingleKidzeeViewholder(LayoutInflater.from(getApplicationContext()).inflate(R.layout.single_item_layout_button,viewGroup,false));
        }

        @Override
        public void onBindViewHolder(@NonNull final SingleKidzeeViewholder singleKidzeeViewholder, final int i) {


            final char ch = RandomString.charAt(i);
            singleKidzeeViewholder.SingleItem.setText(String.valueOf(ch));
            SingleItemJumbledRecyclerView.setVisibility(View.VISIBLE);

            singleKidzeeViewholder.SingleItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(ch==(OrgString.charAt(count)))
                    {


                        if(WrongView.isShown())
                        {
                            WrongView.setVisibility(View.GONE);
                        }
                        count++;

                        singleKidzeeViewholder.SingleItem.setVisibility(View.GONE);
                        MainTextToSpeech.speak(String.valueOf(ch),TextToSpeech.QUEUE_ADD,null);
                        correctAdapter= new CorrectAdapter(OrgString,count);
                        correctAdapter.notifyDataSetChanged();
                        SingleItemCorrectRecyclerView.setAdapter(correctAdapter);


                    }
                    else
                    {
                        ShowCustomToast(0);
                        MainTextToSpeech.speak("Wrong",TextToSpeech.QUEUE_ADD,null);
                    }

                    if(count==OrgString.length())
                    {
                        MainTextToSpeech.speak(OrgString,TextToSpeech.QUEUE_ADD,null);
                        SingleItemJumbledRecyclerView.clearAnimation();
                        SingleItemJumbledRecyclerView.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),android.R.anim.fade_out));
                        SingleItemJumbledRecyclerView.getAnimation().start();
                        SingleItemJumbledRecyclerView.setVisibility(View.INVISIBLE);
                        findViewById(R.id.tick).setVisibility(View.VISIBLE);
                        findViewById(R.id.tick_card).setVisibility(View.VISIBLE);


                    }


                }
            });
        }


        @Override
        public int getItemCount() {

            return RandomString.length();
        }

        public class SingleKidzeeViewholder extends RecyclerView.ViewHolder {

            Button SingleItem;

            public SingleKidzeeViewholder(@NonNull View itemView) {
                super(itemView);

                SingleItem = itemView.findViewById(R.id.single_item_button);
            }
        }
    }

    private void ShowCustomToast(int i) {

        WrongView.setVisibility(View.VISIBLE);
        WrongToast.setDuration(i);
        WrongToast.show();
    }


    @Override
    protected void onDestroy() {

        if(MainTextToSpeech!=null)
        {
            MainTextToSpeech.stop();
            MainTextToSpeech.shutdown();
        }
        super.onDestroy();
    }

    private class CorrectAdapter extends RecyclerView.Adapter<CorrectAdapter.CorrectViewholder> {

        String CorrectString;
        int correctlength;

        public CorrectAdapter(String correctString, int correctlength) {
            CorrectString = correctString;
            this.correctlength = correctlength;
        }

        @NonNull
        @Override
        public CorrectViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new CorrectAdapter.CorrectViewholder(LayoutInflater.from(getApplicationContext()).inflate(R.layout.single_item_layout_button,viewGroup,false));
        }

        @Override
        public void onBindViewHolder(@NonNull CorrectViewholder correctViewholder, int i) {

            if(correctlength>i)
            {
                correctViewholder.OrgButton.setText(String.valueOf(CorrectString.charAt(i)));
                correctViewholder.OrgButton.setBackgroundResource(R.drawable.circular_background_green);
            }
            else
            {
                correctViewholder.OrgButton.setText("_");
                correctViewholder.OrgButton.setBackgroundResource(R.drawable.circular_background_red);

            }

        }

        @Override
        public int getItemCount() {

            return CorrectString.length();
        }

        public class CorrectViewholder extends RecyclerView.ViewHolder {

            Button OrgButton;

            public CorrectViewholder(@NonNull View itemView) {
                super(itemView);

                OrgButton = itemView.findViewById(R.id.single_item_button);
            }
        }
    }

   public void showDialogue(){
       CFAlertDialog.Builder builder = new CFAlertDialog.Builder(MainActivity.this)
               .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
               .setTitle("Choose your item")
               .setSingleChoiceItems(new String[]{"Animals",
                       "Fruits",}, 2, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       switch (i){
                           case 0:
                               animalList();
                               dialogInterface.dismiss();


                               break;

                           case 1:
                               fruitList();
                               dialogInterface.dismiss();

                               break;

                       }

                   }
               })
               .setDialogBackgroundColor(MainActivity.this.getResources().getColor(R.color.cfdialog_button_white_text_color))
               .setIcon(R.drawable.ic_check_circle_black_24dp)
               .addButton("  CANCEL ", -1, Color.BLUE, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                   }
               });

       builder.show();




   }
}
