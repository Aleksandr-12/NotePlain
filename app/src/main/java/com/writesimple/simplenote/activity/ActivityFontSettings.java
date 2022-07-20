package com.writesimple.simplenote.activity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RadioGroup;
import com.writesimple.simplenote.R;
import com.writesimple.simplenote.model.ViewModel.UserViewModel;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class ActivityFontSettings extends AppCompatActivity {

    private int mFontSizeTitle;
    private int mFontSizeContent;
    private RadioGroup radioGroup;
    private RadioGroup radioGroupSizeTitle;
    private RadioGroup radioGroupSizeContent;
    private String fontFamily;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_dialog_for_fonts);
        ActionBar actionBar =getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getResources().getString(R.string.fontSettings));
        radioGroup = findViewById(R.id.fontFamily);
        radioGroupSizeTitle = findViewById(R.id.sizeTitle);
        radioGroupSizeContent = findViewById(R.id.sizeContent);

        mFontSizeTitle = getIntent().getIntExtra("mFontSizeTitle",22);
        mFontSizeContent = getIntent().getIntExtra("mFontSizeContent",20);
        radioGroupSizeTitle.check(isCheckSizeTitleDataBase(mFontSizeTitle));
        radioGroupSizeContent.check(isCheckSizeContentDataBase(mFontSizeContent));



        if (getIntent().getStringExtra("mFontFamily") != null){
            fontFamily = getIntent().getStringExtra("mFontFamily");
        }else{
            fontFamily = "verdana";
        }

        radioGroup.check(isCheckIdFromDataBase(fontFamily));
        getFontRadioGroup();
        getSizeTitleRadioGroup();
        getSizeContentRadioGroup();
        Button save;
        save = findViewById(R.id.save);
        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        save.setOnClickListener(view -> {
            if(userViewModel.getBooleanDateSubs()){
                Intent intent = new Intent();
                intent.putExtra("mFontFamily", fontFamily);
                intent.putExtra("mFontSizeTitle", mFontSizeTitle);
                intent.putExtra("mFontSizeContent", mFontSizeContent);
                setResult(RESULT_OK, intent);
                finish();
            }else{
                Intent intent = new Intent(ActivityFontSettings.this, ActivityPremium.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private int isCheckIdFromDataBase(String isCheck) {
        int selectCheck = 0;
        switch (isCheck) {
            case "":
                selectCheck = R.id.verdana;
                break;
            case "verdana":
                selectCheck = R.id.verdana;
                break;
            case "open_sans":
                selectCheck = R.id.open_sans;
            break;
            case "lato":
                selectCheck = R.id.lato;
                break;
            case "ubuntu":
                selectCheck = R.id.ubuntu;
                break;
            case "roboto":
                selectCheck = R.id.roboto;
                break;
            case "source_sans_pro":
                selectCheck = R.id.source_sans_pro;
                break;
            case "nunito":
                selectCheck = R.id.nunito;
            break;
            case "montserrat":
                selectCheck = R.id.montserrat;
                break;
            default:
                break;
        }
        return selectCheck;
    }

    private int isCheckSizeTitleDataBase(int isCheck) {
        int selectCheck = 0;
        switch (isCheck) {
            case 0:
                selectCheck = R.id.two2;
                break;
            case 12:
                selectCheck = R.id.ten2;
                break;
            case 14:
                selectCheck = R.id.ten4;
                break;
            case 16:
                selectCheck = R.id.ten6;
                break;
            case 18:
                selectCheck = R.id.ten8;
                break;
            case 20:
                selectCheck = R.id.two0;
                break;
            case 22:
                selectCheck = R.id.two2;
                break;
            case 24:
                selectCheck = R.id.two4;
                break;
            case 26:
                selectCheck = R.id.two6;
                break;
            case 28:
                selectCheck = R.id.two8;
                break;
            case 30:
                selectCheck = R.id.thirdxeetin;
                break;

            default:
                break;
        }
        return selectCheck;
    }
    private int isCheckSizeContentDataBase(int isCheck) {
        int selectCheck = 0;
        switch (isCheck) {
            case 0:
                selectCheck = R.id.contenttwo2;
                break;
            case 12:
                selectCheck = R.id.contentten2;
                break;
            case 14:
                selectCheck = R.id.contentten4;
                break;
            case 16:
                selectCheck = R.id.contentten6;
                break;
            case 18:
                selectCheck = R.id.contentten8;
                break;
            case 20:
                selectCheck = R.id.contenttwo0;
                break;
            case 22:
                selectCheck = R.id.contenttwo2;
                break;
            case 24:
                selectCheck = R.id.contenttwo4;
                break;
            case 26:
                selectCheck = R.id.contenttwo6;
                break;
            case 28:
                selectCheck = R.id.contenttwo8;
                break;
            case 30:
                selectCheck = R.id.contentthirdxeetin;
                break;

            default:
                break;
        }
        return selectCheck;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressLint("NonConstantResourceId")
    private void getFontRadioGroup() {
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case -1:
                    fontFamily = "verdana";
                    break;
                case R.id.verdana:
                    fontFamily = "verdana";
                    break;
                case R.id.open_sans:
                    fontFamily = "open_sans";
                    break;
                case R.id.lato:
                    fontFamily = "lato";
                    break;
                case R.id.ubuntu:
                    fontFamily = "ubuntu";
                    break;
                case R.id.roboto:
                    fontFamily = "roboto";
                    break;
                case R.id.source_sans_pro:
                    fontFamily = "source_sans_pro";
                    break;
                case R.id.nunito:
                    fontFamily = "nunito";
                    break;
                case R.id.montserrat:
                    fontFamily = "montserrat";
                    break;

                default:
                    break;
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    private void getSizeTitleRadioGroup() {
        radioGroupSizeTitle.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case -1:
                    mFontSizeTitle = 22;
                    break;
                case R.id.ten2:
                    mFontSizeTitle = 12;
                    break;
                case R.id.ten4:
                    mFontSizeTitle = 14;
                    break;
                case R.id.ten6:
                    mFontSizeTitle = 16;
                    break;
                case R.id.ten8:
                    mFontSizeTitle = 18;
                    break;
                case R.id.two0:
                    mFontSizeTitle = 20;
                    break;
                case R.id.two2:
                    mFontSizeTitle = 22;
                    break;
                case R.id.two4:
                    mFontSizeTitle = 24;
                    break;
                case R.id.two6:
                    mFontSizeTitle = 26;
                    break;
                case R.id.two8:
                    mFontSizeTitle = 28;
                    break;
                case R.id.thirdxeetin:
                    mFontSizeTitle = 30;
                    break;
                default:
                    break;
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    private void getSizeContentRadioGroup() {
        radioGroupSizeContent.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case 0:
                    mFontSizeContent = 22;
                    break;
                case R.id.contentten2:
                    mFontSizeContent = 12;
                    break;
                case R.id.contentten4:
                    mFontSizeContent = 14;
                    break;
                case R.id.contentten6:
                    mFontSizeContent = 16;
                    break;
                case R.id.contentten8:
                    mFontSizeContent = 18;
                    break;
                case R.id.contenttwo0:
                    mFontSizeContent = 20;
                    break;
                case R.id.contenttwo2:
                    mFontSizeContent = 22;
                    break;
                case R.id.contenttwo4:
                    mFontSizeContent = 24;
                    break;
                case R.id.contenttwo6:
                    mFontSizeContent = 26;
                    break;
                case R.id.contenttwo8:
                    mFontSizeContent = 28;
                    break;
                case R.id.contentthirdxeetin:
                    mFontSizeContent = 30;
                    break;
                default:
                    break;
            }
        });
    }
}
