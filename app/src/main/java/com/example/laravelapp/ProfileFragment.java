package com.example.laravelapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class  ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    TextView tvNameP,tvEmailP,tvCreatedP;
    Button btnLogoutP;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile, container, false);

        tvNameP=view.findViewById(R.id.tvname_profile);
        tvEmailP=view.findViewById(R.id.tvemail_profile);
        tvCreatedP=view.findViewById(R.id.tvcreated_profile);
        btnLogoutP=view.findViewById(R.id.btnLogoutP);

        getUser();

        btnLogoutP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        return view;
    }

    private void getUser() {
        String url = getString(R.string.api_server) + "/user";
        new Thread(new Runnable() {
            @Override
            public void run() {
                Http http = new Http(requireContext(), url);
                http.setToken(true);
                http.send();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Integer code = http.getStatusCode();
                        if (code == 200) {
                            try {
                                JSONObject response = new JSONObject(http.getResponse());
                                String name = response.getString("name");
                                String email = response.getString("email");
                                String create_at = response.getString("created_at");

                                tvNameP.setText(name);
                                tvEmailP.setText(email);
                                tvCreatedP.setText(create_at);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            Toast.makeText(requireContext(), "Error "+code, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    private void logout(){
        String url=getString(R.string.api_server)+"/logout";
        new Thread(new Runnable() {
            @Override
            public void run() {
                Http http=new Http(requireContext(),url);
                http.setMethod("post");
                http.setToken(true);
                http.send();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Integer code= http.getStatusCode();
                        if(code==200){
                            Intent intent=new Intent(requireContext(),MainActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(requireContext(), "Error "+code, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        }).start();
    }
}