package com.example.user.banhangonline.screen.myGioHang;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.user.banhangonline.interactor.prefer.PreferManager;
import com.example.user.banhangonline.model.DonHang;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.user.banhangonline.utils.KeyUntils.keyDonHang;

public class MyGioHangPresenter implements MyGioHangContact.Presenter {
    private MyGioHangContact.View mView;
    private Context context;
    private List<DonHang> mList;

    public void setContext(Context context) {
        this.context = context;
    }


    public List<DonHang> getmList() {
        return mList;
    }

    public void setmList(List<DonHang> mList) {
        this.mList = mList;
    }

    @Override
    public void attachView(MyGioHangContact.View View) {
        mView = View;
        mList = new ArrayList<>();
    }

    @Override
    public void detach() {
        mView = null;
    }

    @Override
    public boolean isViewAttached() {
        return mView != null;
    }

    @Override
    public void getListCartFromFirebase(DatabaseReference databaseReference) {
        if (!isViewAttached()) return;
        if (context == null) return;
        mList.clear();
        databaseReference.child(keyDonHang).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DonHang donHang = dataSnapshot.getValue(DonHang.class);
                if (donHang != null) {
                    if (donHang.getIdNguoiBan() != null && PreferManager.getUserID(context) != null) {
                        if (donHang.getIdNguoiBan().equals(PreferManager.getUserID(context))) {
                            mList.add(donHang);
                        }
                    }
                }


                if (mList != null) {
                    if (mView != null) {
                        mView.getCartSuccess();
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (mView != null) {
                    mView.getCartError();
                }
            }
        });
    }


    @Override
    public void deleteDonHang(final DatabaseReference databaseReference, final DonHang donHang) {
        if (!isViewAttached()) return;
        if (donHang == null) return;
        databaseReference.child(keyDonHang).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DonHang donHang1 = snapshot.getValue(DonHang.class);
                    if (PreferManager.getUserID(context).equals(donHang.getIdNguoiBan())) {
                        if (donHang1.getIdDonHang().equals(donHang.getIdDonHang())) {
                            databaseReference.child(keyDonHang).child(snapshot.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        if (mView != null) {
                                            mView.deleteDonHangSuccess();
                                            return;
                                        }
                                    } else {
                                        if (mView != null) {
                                            mView.deleteDonHangError();
                                        }
                                    }
                                }
                            });
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


}
