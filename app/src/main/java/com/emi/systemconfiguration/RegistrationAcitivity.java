package com.emi.systemconfiguration;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class RegistrationAcitivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String prevStarted = "yes";

    private DatePicker datePicker;
    private Calendar calendar;
    private TextView dateView, endDateView , costLabel;
    private int year, month, day;

    //Firebase auth
    private ProgressBar progressbar;
    private FirebaseAuth mAuth;

    // creating variables for our edit text
    private EditText  policyId, device_amount, customer_uidEdit, customer_nameEdit, customer_contactEdit, customer_emailEdit, customer_mobile_brandEdit, customer_paymentEdit, customer_loanEdit;

    // creating variable for button
    private Button registerBtn;



    boolean isAllFieldsChecked = false, planValid = false;

    // creating a strings for storing
    // our values from edittext fields.
    private String plan, policy_no,customer_uid, customer_name, customer_contact, customer_email, customer_mobile_brand, customer_payment, customer_loan, VendorID, PolicyNo, startDate, endDate,amount;

    // creating a variable
    // for firebasefirestore.
    private FirebaseFirestore db;

    Spinner spinner, spinnerPlan, spinner2;

    CheckBox planView, loanView;

    List<String> policyDocID = new ArrayList<String>();

    List<Map<String, Object>> userData = new ArrayList<java.util.Map<String, Object>>();

//    @Override
//    protected void onResume() {
//        super.onResume();
//        SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
//        if (!sharedpreferences.getBoolean(prevStarted, false)) {
//            SharedPreferences.Editor editor = sharedpreferences.edit();
//            editor.putBoolean(prevStarted, Boolean.TRUE);
//            editor.apply();
//        } else {
//            moveToSecondary();
//        }
//    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_acitivity);


        ActionBar actionBar = getSupportActionBar(); // or getActionBar();
        getSupportActionBar().setTitle("Emi-Locker"); // set the top title
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.system_icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        String title = actionBar.getTitle().toString(); // get the title
        actionBar.hide(); // or even hide the actionbar

        //Calender View
        dateView = (TextView) findViewById(R.id.dateTextView);
        dateView.setEnabled(false);
        endDateView = (TextView) findViewById(R.id.endDateView);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month+1, day);

        // taking FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        // getting our instance
        // from Firebase Firestore.
        db = FirebaseFirestore.getInstance();

        //        Fetch All the Policy Id
        getPolicyIdList();


        //Spinner brand
        spinner = findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.brands, R.layout.color_spinner_layout);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


//        if(radiobutton1.isChecked() == false){
//            radiobutton1.setChecked(true);
//            spinner2.setVisibility(View.INVISIBLE);
//        }else{
//            radiobutton2.setChecked(false);
//        }



        //Spinner Loan
        spinner2 = findViewById(R.id.spinner3);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.loan, R.layout.color_spinner_layout);
        adapter2.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(this);
        spinner2.setVisibility(View.GONE);

//        Spinner Plan
        spinnerPlan = findViewById(R.id.spinnerPlan);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this, R.array.plans, R.layout.color_spinner_layout);
        adapter3.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        spinnerPlan.setAdapter(adapter3);
        spinnerPlan.setOnItemSelectedListener(this);
        spinnerPlan.setVisibility(View.GONE);


//        planView = (CheckBox)findViewById(R.id.cashBox);
//        planView.setVisibility(View.GONE);
        loanView = (CheckBox)findViewById(R.id.loanBox);
        costLabel =(TextView) findViewById(R.id.costLabel);
        device_amount = (EditText) findViewById(R.id.amount);

//        planView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(((CompoundButton) view).isChecked()){
//                    spinner2.setVisibility(View.GONE);
//                    loanView.setChecked(false);
//                    customer_payment = planView.getText().toString();
//                    spinnerPlan.setVisibility(View.VISIBLE);
//                    costLabel.setVisibility(View.VISIBLE);
//                    device_amount.setVisibility(View.VISIBLE);
//                }
//                else {
//                    spinner2.setVisibility(View.VISIBLE);
//                    spinnerPlan.setVisibility(View.GONE);
//                    loanView.setChecked(true);
//                }
//            }
//        });
        loanView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((CompoundButton) view).isChecked()){
                    spinner2.setVisibility(View.VISIBLE);
//                    planView.setChecked(false);
//                    spinnerPlan.setVisibility(View.GONE);
//                    customer_payment = planView.getText().toString();
                    planValid = true;
//                    costLabel.setVisibility(View.GONE);
//                    device_amount.setVisibility(View.GONE);
                }
                else
                {
                    spinner2.setVisibility(View.GONE);
//                    spinnerPlan.setVisibility(View.VISIBLE);
//                    planView.setChecked(true);
                    costLabel.setVisibility(View.VISIBLE);
                    device_amount.setVisibility(View.VISIBLE);
                }
            }
        });


        // initializing our edittext and buttons
        customer_nameEdit = findViewById(R.id.customerName);
        customer_emailEdit = findViewById(R.id.customerMail);
        customer_contactEdit = findViewById(R.id.customerNumber);
        registerBtn = findViewById(R.id.registerBtn);



        // adding on click listener for button
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isAllFieldsChecked = CheckAllFields();

                // getting data from edittext fields
                customer_name = customer_nameEdit.getText().toString();
                customer_contact = customer_contactEdit.getText().toString();
                customer_email = customer_emailEdit.getText().toString();

                if (isAllFieldsChecked ) {
                    if(VendorID.length() > 0){
                        registerNewUser();
                    }

                   else{
                       toastMessage("Policy is empty");
                    }
//                    toastMessage("done workin");
                }



//
//                // validating the text fields if empty or not.
//                if (TextUtils.isEmpty(customer_name)) {
//                    customer_nameEdit.setError("Please enter Name");
//                } else if (TextUtils.isEmpty(customer_contact)) {
//                    customer_contactEdit.setError("Please Enter number");
//                } else if (TextUtils.isEmpty(customer_email)) {
//                    customer_emailEdit.setError("Please enter  Duration");
//                } else {
//                    // calling method to add data to Firebase Firestore.
//                    registerNewUser();
////                    registerUser();
//                }
            }
        });





        policyId = (EditText) findViewById(R.id.policyId);

        policyId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                TextView policyDetails =(TextView) findViewById(R.id.vendorName);
//                policyDetails.setText(s);
//                toastMessage( s.toString().length() + " "+ s.toString());
//                if(s.toString().length() == 7){
//                    getvendorId(s.toString());
//                }
                PolicyNo = s.toString();
                if(s.length() >= 1){
                    getvendorId(s.toString().toUpperCase());
                    policy_no = s.toString().toUpperCase();
//                    toastMessage(s.toString());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
//                int size= s.length();
//                toastMessage(String.valueOf(s.length()));
//                PolicyNo = s.toString();
//                if(s.length() >= 1){
//                    getvendorId(s.toString().toUpperCase());
//                    policy_no = s.toString().toUpperCase();
////                    toastMessage(s.toString());
//                }
//                getvendorId(s.toString());
//                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }
        });

        device_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

//                if (s.length() > 2 && planView.isChecked()) {
//                    int textAmount = Integer.parseInt(s.toString());
//                    if (plan.equals("220")) {
//                        Log.d("Amount", s.toString());
//
//                        if (textAmount >= 6000 && textAmount <= 9990  ) {
//                            toastMessage("The amount is valid with 220 plan");
//                            planValid= true;
//                        }
//                        else{
//                            toastMessage("The amount is not valid with 220 plan");
//                            planValid=false;
//                        }
//                    } else if (plan.equals("550")) {
//                        if (textAmount >= 9990 && textAmount <= 12490 ) {
//                            toastMessage("The amount is valid with 550 plan");
//                            planValid= true;
//                        } else{
//                            toastMessage("The amount is not valid with 550 plan");
//                            planValid=false;
//                        }
//
//                    } else if (plan.equals("880")) {
//
//                        if (textAmount >= 12490 && textAmount <= 17490 ) {
//                            toastMessage("The amount is valid with 880 plan");
//                            planValid= true;
//                        } else{
//                            toastMessage("The amount is not valid with 880 plan");
//                            planValid=false;
//                        }
//                    }
//                    else if (plan.equals("1100")) {
//                        if (textAmount >= 17490) {
//                            toastMessage("The amount is valid with 1100 plan");
//                            planValid= true;
//                        } else{
//                            toastMessage("The amount is not valid with 1100 plan");
//                            planValid=false;
//                        }
//                    }
//                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    public void moveToSecondary(){
        // use an intent to travel from one activity to another.
        Intent intent;
        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }



    private void getvendorId(String policiesNo) {
//        toastMessage(policyDocID.toString());
//        for (int i=0; i < policyDocID.size(); i++) {
//            System.out.println(policyDocID.get(i));
//            toastMessage(policyDocID.get(i));
        VendorID = "";
        TextView vendorName = (TextView) findViewById(R.id.vendorName);
        vendorName.setText("Vendor Name : ");
        TextView vendorShopName = (TextView) findViewById(R.id.vendorShopName);
        vendorShopName.setText("Shop Name : " );
        TextView vendorShopCode = (TextView) findViewById(R.id.vendorShopCode);
        vendorShopCode.setText("Shop Code : ");
        TextView vendorContact = (TextView) findViewById(R.id.vendorContact);
        vendorContact.setText("Vendor Contact : ");
        TextView vendorEmail = (TextView) findViewById(R.id.vendorEmail);
        vendorEmail.setText("Email Id : " );
        TextView vendorAddress = (TextView) findViewById(R.id.vendorAddress);
        vendorAddress.setText("Shop Address : ");

        db.collection("policy").whereEqualTo("policyNo", policiesNo)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
//                            toastMessage("Success found");
                        Log.d("Success","Suucfull ofund ==========================");
                            for (QueryDocumentSnapshot document : task.getResult()) {

//                                toastMessage(document.getData().toString());
                                Log.d("2Success","Suucfull ofund ==========================");
                                if (!document.getData().containsKey("customerUid") && document.getData().containsKey("vendorID")) {

                                    String vendorId = document.getData().get("vendorID").toString();
                                    VendorID = vendorId;

                                    db.collection("vendors").document(vendorId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                            if (error != null) {
                                                // this method is called when error is not null
                                                // and we gt any error
                                                // in this cas we are displaying an error message.
                                                toastMessage("Error found" + error);
                                                return;
                                            }
                                            if (value != null && value.exists()) {
                                                if(value.getId().equals(VendorID)) {
                                                    //   toastMessage(value.getData().toString());
                                                    TextView vendorName = (TextView) findViewById(R.id.vendorName);
                                                    vendorName.setText("Vendor Name : " + value.getData().get("contactperson").toString());
                                                    TextView vendorShopName = (TextView) findViewById(R.id.vendorShopName);
                                                    vendorShopName.setText("Shop Name : " + value.getData().get("shopname").toString());
                                                    TextView vendorShopCode = (TextView) findViewById(R.id.vendorShopCode);
                                                    vendorShopCode.setText("Shop Code : " + value.getData().get("vendorcode").toString());
                                                    TextView vendorContact = (TextView) findViewById(R.id.vendorContact);
                                                    vendorContact.setText("Vendor Contact : " + value.getData().get("contact").toString());
                                                    TextView vendorEmail = (TextView) findViewById(R.id.vendorEmail);
                                                    vendorEmail.setText("Email Id : " + value.getData().get("email").toString());
                                                    TextView vendorAddress = (TextView) findViewById(R.id.vendorAddress);
                                                    vendorAddress.setText("Shop Address : " + value.getData().get("address").toString() + "," + value.getData().get("location").toString());
                                                }
                                            }

                                        }
                                    });
                                }
                            }
                        }
                        else{
                            Log.d("Policy","Policy not found");
                            toastMessage("policy not found");

                        }
                    }
                });
    }
//                    .document(policyDocID.get(i));

//            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                @Override
//                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                    if (error != null) {
//                        // this method is called when error is not null
//                        // and we gt any error
//                        // in this cas we are displaying an error message.
//                        toastMessage("Error found" + error);
//                        return;
//                    }
//                    if (value != null && value.exists()) {
//                        // if th value from firestore is not null then we are getting
//                        // our data and setting that data to our text view.
////                        toastMessage(value.getData().get("policyNo").toString() + (value.getData().get("vendorID").toString() ));
////                          toastMessage(policiesNo + value.getData().get("customerUid").toString()); && !value.getData().containsKey("customerUid")
//                        Log.d("Customer", String.valueOf(value.getData()));
//
//                        if(value.getData().get("policyNo").toString().equals(policy_no)  ){
//
//
//                        }
//                        else
//                        {
////                            toastMessage("policyNo not found" + policiesNo);
//                            Log.d("Not Found : " , "Poloici not found");
////                            policyId.setError("Policy not found");
//                        }
//
//                    }
//
//                }

//            });
//
//        }

//    }



    private void getPolicyIdList() {

        db.collection("policy").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> list = new ArrayList<>();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        list.add(document.getId());
                        policyDocID.add(document.getId());
//                        Log.d("this id are", String.valueOf(document.getData()));
//
//                        toastMessage(document.getData()));
                    }
//                    toastMessage(policyDocID.toString());
                    Log.d("List are", policyDocID.toString());
                } else {
                    Log.d("Error", "Error getting documents: ", task.getException());
                }
            }
        });

//        db.collection("policy").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot documentSnapshots) {
//                if (documentSnapshots.isEmpty()) {
//                    toastMessage("onSuccess: LIST EMPTY");
//                    return;
//                } else {
//                    // Convert the whole Query Snapshot to a list
//                    // of objects directly! No need to fetch each
//                    // document.
//                    toastMessage("its work");
//
////                    List String = documentSnapshots.toObjects();
////                    // Add all to your list
////                    mArrayList.addAll(types);
////                    Log.d("List of elemets", "onSuccess: " + mArrayList);
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                toastMessage("Error getting data!!!");
//            }
//        });
    }

    public void toastMessage(String message){
        Toast.makeText(getApplicationContext(), message,Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        String text = parent.getItemAtPosition(position).toString();
        String Brand = spinner.getSelectedItem().toString();
        //  String paymentMode = .getSelectedItem().toString();
        String loan = spinner2.getSelectedItem().toString();
//        Toast.makeText(parent.getContext(),Brand, Toast.LENGTH_SHORT).show();
        customer_mobile_brand = Brand;
        //   customer_payment = paymentMode;
        customer_loan = loan;

        plan = spinnerPlan.getSelectedItem().toString();
    }



    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

//    private void registerUser(){
//
//        String email, password;
//        email = customer_emailEdit.getText().toString();
//        password = customer_contactEdit.getText().toString();
//
//        //creating a new user
//        mAuth.createUserWithEmailAndPassword(email,password )
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//
//                        //checking if success
//                        if(task.isSuccessful()){
//
//                            //display some message here
//                            Toast.makeText(getApplicationContext(),
//                                    "Authenticationn Successful",
//                                    Toast.LENGTH_LONG)
//                                    .show();
//                        }else{
//                            //display some message here
//                            Toast.makeText(getApplicationContext(),
//                                    "Authenticationn faillll",
//                                    Toast.LENGTH_LONG)
//                                    .show();
//                        }
//
//                    }
//                });
//
//    }

    private void registerNewUser()
    {
        // show the visibility of progress bar to show loading
//        progressbar.setVisibility(View.VISIBLE);

        // Take the value of two edit texts in Strings
        String email, password;
        email = customer_emailEdit.getText().toString();
        password = customer_contactEdit.getText().toString();

        // Validations for input email and password
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(),
                    "Please enter email!!",
                    Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(),
                    "Please enter password!!",
                    Toast.LENGTH_LONG)
                    .show();
            return;
        }
//        Toast.makeText(getApplicationContext(),
//                email + password,
//                Toast.LENGTH_LONG).show();


        // create new user or register new user
        mAuth
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {

                        if (task.isSuccessful()) {

//                            Toast.makeText(getApplicationContext(),
//                                    "Authentication successful!" + mAuth.getCurrentUser().getUid(),
//                                    Toast.LENGTH_LONG)
//                                    .show();
//                            MainActivity main = new MainActivity();

            //                customer_uid  = mAuth.getCurrentUser().getUid();
                            customer_uid =  MainActivity.getDeviceId(getApplicationContext());

                            amount= device_amount.getText().toString();
                            addDataToFirestore(customer_uid,customer_name, customer_contact, customer_email, customer_mobile_brand, customer_payment, customer_loan,VendorID, PolicyNo, startDate, endDate, amount, plan);


                            // hide the progress bar
//                            progressBar.setVisibility(View.GONE);

                            // if the user created intent to login activity
//                            Intent intent
//                                    = new Intent(RegistrationActivity.this,
//                                    MainActivity.class);
//                            startActivity(intent);
                        }
                        else {

                            // Registration failed
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Authentication failed!!" + " Please try again later",
                                    Toast.LENGTH_LONG)
                                    .show();

                            // hide the progress bar
//                            progressBar.setVisibility(View.GONE);GONE
                        }
                    }
                });
    }

    private void addDataToFirestore(String customer_uid,String customer_name,String customer_contact,String customer_email, String customer_mobile_brand,String customer_payment,String customer_loan, String vendorID, String policyNo, String startDate,String endDate, String amount, String anti_theft_plan) {

        // creating a collection reference
        // for our Firebase Firetore database.
        CollectionReference dbRegister = db.collection("users");

        // adding our data to our courses object class.
        RegistrationDetails registration = new RegistrationDetails(customer_uid,customer_name, customer_contact, customer_email, customer_mobile_brand, customer_payment, customer_loan, startDate, endDate, amount, anti_theft_plan);

        // below method is use to add data to Firebase Firestore.
        dbRegister.document(customer_uid).set(registration).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void avoid) {
                // after the data addition is successful
                // we are displaying a success toast message.
                for (int i=0; i < policyDocID.size(); i++) {
                    DocumentReference documentReference = db.collection("policy").document(policyDocID.get(i));
                    String policyDocumentsID = policyDocID.get(i);
                    documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {

                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                // this method is called when error is not null
                                // and we gt any error
                                // in this cas we are displaying an error message.
                                toastMessage("Error found" + error);
                                return;
                            }
                            if (value != null && value.exists()) {

                                if(value.getData().get("policyNo").toString().equals(policy_no)){

//                           toastMessage( policyDocumentsID.toString());
                                    CollectionReference dbPolicy = db.collection("policy");

                                    dbPolicy.document( policyDocumentsID.toString()).update("customerUid",customer_uid);

                                    db.collection("vendors").document(vendorID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> vendorDetails) {
                                            Vendor.number =  vendorDetails.getResult().get("number").toString();
                                        }
                                    });

//                                    dbPolicy.document(policyDocumentsID.toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                            Log.d("VendorId",task.getResult().getData().toString());
//                                            String vendorId = task.getResult().get("vendorID").toString();
//
//                                        }
//                                    });

                                }
                                else
                                {
//                           toastMessage("policyNo not found" + policiesNo);
                                    Log.e("Not Found : " , "Error no update in policy collection");
                                }

                            }

                        }

                    });

                }

                toastMessage("Registration is done successfully");
                Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivityIntent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // this method is called when the data addition process is failed.
                // displaying a toast message when data addition is failed.
                toastMessage("Failed to register details Please try after some time \n" + e);
            }
        });

//        For Updating data in policy Firebase

    }
    public void updateInPolicy(View view){


    }

    //    Date functionalities
    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
//        Toast.makeText(getApplicationContext(), "ca",
//                Toast.LENGTH_SHORT)
//                .show();
    }
    @SuppressWarnings("deprecation")
    public void endDate(View view) {
        showDialog(988);
//        Toast.makeText(getApplicationContext(), "ca",
//                Toast.LENGTH_SHORT)
//                .show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        else if(id == 988) {
            return new DatePickerDialog(this,
                    endDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2+1, arg3);
                }
            };
    private DatePickerDialog.OnDateSetListener endDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showEndDate(arg1, arg2+1, arg3);
                }
            };

    private void showDate(int year, int month, int day) {
        dateView.setText("Start Date : "+ new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
        startDate =  new StringBuilder().append(day).append("/").append(month).append("/").append(year).toString();
    }
    private void showEndDate(int year, int month, int day) {
        endDateView.setText("End Date : "+ new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
        endDate =  new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year).toString();
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    private Object activeUser(){
        final String[] status = new String[1];

     String deviceId = MainActivity.getDeviceId(this);

        return deviceId;
//        status[0]= deviceId;

//        DocumentReference documentReference = db.collection("users").document(deviceId);
//        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                if (error != null) {
//                    // this method is called when error is not null
//                    // and we gt any error
//                    // in this cas we are displaying an error message.
//                    Log.d("Error is","Error found" + error);
//                    return;
//                }
//                if (value != null && value.exists()) {
//
//                     userData.add(value.getData());
//
//                    Log.d("Found the", value.getData().get("customer_active").toString());
//
//                }
//            }
//        });


//        DocumentReference documentReference = db.collection("users").document(MainActivity.getDeviceId(getApplicationContext()));
//        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    // Document found in the offline cache
//                    DocumentSnapshot document = task.getResult();
//                    Log.d("Active Device", "Cached document data: " + document.getData());
////                    toastMessage(document.getData().toString());
//                } else {
//                    Log.d("Active device", "Cached get failed: ", task.getException());
//                }
//
//            }
//        });
    };

    private boolean CheckAllFields(){
        if(customer_nameEdit.length() == 0){
            customer_nameEdit.setError("Required");
            return false;
        }
        if(customer_emailEdit.length() == 0){
            customer_emailEdit.setError("Required");
            return false;
        }
        String email = customer_emailEdit.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if(!customer_emailEdit.getText().toString().matches(emailPattern)){
            customer_emailEdit.setError("Enter Valid Email");
            return false;
        }

        if(customer_contactEdit.length() == 0){
            customer_contactEdit.setError("Required");
            return false;
        }
        else if(customer_contactEdit.length()  <= 9 || customer_contactEdit.length() >= 11){
            customer_contactEdit.setError("Enter Valid Number");
            return false;
        }

        if(spinner.getSelectedItem().toString().equals("Select brand")){
           toastMessage("Select the device Brand");
            return false;
        }

        if(device_amount.length() == 0){
            device_amount.setError("Required");
            return false;
        }
        if(endDateView.getText().toString().contains("Select end date"))
        {
           toastMessage("Set the end date");
            return false;
        }
        if(policyId.length() == 0){
            policyId.setError("Enter Policy");
            return false;
        }





        return true;
    };


}

