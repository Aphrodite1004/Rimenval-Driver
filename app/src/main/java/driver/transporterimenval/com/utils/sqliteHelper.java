package driver.transporterimenval.com.utils;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by redixbit on 03-11-2018.
 */

public class sqliteHelper extends SQLiteAssetHelper {
    private static final String DB_NAME = "restaurant.db";
    private static final int DB_VER = 1;

    public sqliteHelper(Context context) {
        super(context, DB_NAME, null,DB_VER );
    }

/*

    public static class sqliteCategoryAsync extends AsyncTask<Void, Void, List<categoryModel>> {
        List<delete_exercise_or_cat_model> del_cat_list;
        int id = -1;
        WeakReference<Context> context;
        sqliteHelper helper;
        getStatus Interfacestatus;
        getData Interfacedata;
        String functionName;
        List<categoryModel> catList;
        SQLiteDatabase db;
        Boolean insertorUpdateStatus = false;

        //for insert
        public sqliteCategoryAsync(Context context, String functionName, List<categoryModel> bModel, getStatus Interfacestatus) {
            this.context = new WeakReference<>(context);
            this.Interfacestatus = Interfacestatus;
            this.functionName = functionName;
            this.catList = bModel;
        }

        //for getdata
        public sqliteCategoryAsync(Context context, String functionName, int id, getData Interfacedata) {
            this.context = new WeakReference<>(context);
            this.Interfacedata = Interfacedata;
            this.functionName = functionName;
            this.id = id;
            catList = new ArrayList<>();
        }

        //for delete
        public sqliteCategoryAsync(Context context, List<delete_exercise_or_cat_model> del_exe_list, String functionName, sqliteCategoryAsync.getStatus Interfacestatus) {
            this.context = new WeakReference<>(context);
            this.Interfacestatus = Interfacestatus;
            this.functionName = functionName;
            this.del_cat_list = del_exe_list;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<categoryModel> doInBackground(Void... voids) {
            helper = new sqliteHelper(context.get());
            db = helper.getWritableDatabase();
            switch (functionName) {
                case utilHelper.FUN_INSERT_CAT:
                    insertCategory();
                    break;
                case utilHelper.FUN_GET_CAT:
                    getCategory();
                    break;
                case utilHelper.FUN_UPDATE_TIMESTAMP:
                    updateTimestamp();
                    break;
                case utilHelper.FUN_DEL_CAT:
                    delCat();
                    break;
            }
            return catList;
        }

        @Override
        protected void onPostExecute(List<categoryModel> categoryModels) {
            if (Interfacestatus != null)
                Interfacestatus.status(insertorUpdateStatus);
            if (Interfacedata != null)
                Interfacedata.getCategoryList(categoryModels);
            super.onPostExecute(categoryModels);
        }

        void insertCategory() {
            int newCat = 0, insCat = 0;
            if (!catList.isEmpty()) {
                int size = catList.size();
                Log.e("sqlite: cat list size", String.valueOf(size));
                for (int i = 0; i < size; i++) {
                    if (isCatExists(catList.get(i).getId())) {
                        newCat++;
                        ContentValues cv = new ContentValues();
                        cv.put(utilHelper.F_CAT_ID, catList.get(i).getId());
                        cv.put(utilHelper.F_CAT_NAME, catList.get(i).getTitle());
                        cv.put(utilHelper.F_CAT_IMG, catList.get(i).getImg_name());
                        cv.put(utilHelper.F_CAT_TOT_CAL, catList.get(i).getCal());
                        cv.put(utilHelper.F_CAT_TOT_EXE, catList.get(i).getExercises());
                        cv.put(utilHelper.F_CAT_TIMESTAMP, catList.get(i).getTimestamp());
                        long res = db.insert(utilHelper.TB_CAT_NAME, null, cv);
                        if (res != -1) {
                            insCat++;
                        }
                    } else {
                        //update cat
                        ContentValues cv = new ContentValues();
                        cv.put(utilHelper.F_CAT_ID, catList.get(i).getId());
                        cv.put(utilHelper.F_CAT_NAME, catList.get(i).getTitle());
                        cv.put(utilHelper.F_CAT_IMG, catList.get(i).getImg_name());
                        cv.put(utilHelper.F_CAT_TOT_CAL, catList.get(i).getCal());
                        cv.put(utilHelper.F_CAT_TOT_EXE, catList.get(i).getExercises());
                        cv.put(utilHelper.F_CAT_TIMESTAMP, catList.get(i).getTimestamp());
                        long res = db.update(utilHelper.TB_CAT_NAME, cv, utilHelper.F_CAT_ID + "= '" + catList.get(i).getId() + "'", null);
                        if (res != -1)
                            insertorUpdateStatus = true;
                    }
                }
            }
            if (newCat != 0 && insCat != 0) {
                if (newCat == insCat) {
                    insertorUpdateStatus = true;
                    //dowanload images from server
                    utilHelper.dowanloadImagesOffline(context.get(), catList);
                }
            }
            db.close();
        }

        private void getCategory() {
            Cursor cursor;
            if (id == -1) {
                cursor = db.rawQuery("SELECT * FROM " + utilHelper.TB_CAT_NAME, null);
            } else {
                cursor = db.rawQuery("SELECT * FROM " + utilHelper.TB_CAT_NAME + " WHERE " + utilHelper.F_CAT_ID + " = " + id, null);
            }

            while (cursor.moveToNext()) {
                categoryModel model = new categoryModel();
                model.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(utilHelper.F_CAT_ID))));
                model.setTitle(cursor.getString(cursor.getColumnIndex(utilHelper.F_CAT_NAME)));
                model.setImg_name(cursor.getString(cursor.getColumnIndex(utilHelper.F_CAT_IMG)));
                model.setCal(cursor.getString(cursor.getColumnIndex(utilHelper.F_CAT_TOT_CAL)));
                model.setExercises(cursor.getString(cursor.getColumnIndex(utilHelper.F_CAT_TOT_EXE)));
                model.setTimestamp(cursor.getString(cursor.getColumnIndex(utilHelper.F_CAT_TIMESTAMP)));
                catList.add(model);
            }
            cursor.close();
            db.close();
        }

        private Boolean isCatExists(int id) {
            Boolean status = false;
            Cursor cursor = db.rawQuery("SELECT * FROM " + utilHelper.TB_CAT_NAME + " WHERE " + utilHelper.F_CAT_ID + " = " + id, null);
            if (!cursor.moveToNext())
                status = true;
            cursor.close();
            return status;
        }

        private void updateTimestamp() {
            int id = catList.get(0).getId();
            String timestamp = catList.get(0).getTimestamp();
            ContentValues cv = new ContentValues();
            cv.put(utilHelper.F_CAT_TIMESTAMP, timestamp);
            long res = db.update(utilHelper.TB_CAT_NAME, cv, utilHelper.F_CAT_ID + "= " + id, null);
            if (res != -1)
                insertorUpdateStatus = true;
            db.close();
        }


        private void delCat() {
            if (!del_cat_list.isEmpty()) {
                int size = del_cat_list.size();
                Log.e("sqlite: cat list size", String.valueOf(size));
                for (int i = 0; i < size; i++) {
                    if (!isCatExists(Integer.parseInt(del_cat_list.get(i).getCat_id()))) {
                        String imgname = getCategoryName(Integer.parseInt(del_cat_list.get(i).getCat_id()));
                        if (!imgname.equals("no")) {
                            utilHelper.deleteFileFromInternalStorage(context.get(), imgname, "image");
                            Log.e("deleted Img", imgname);
                        }


                        long res = db.delete(utilHelper.TB_CAT_NAME, utilHelper.F_CAT_ID + "= " + del_cat_list.get(i).getCat_id() + "", null);
                        if (res != -1) {
                            insertorUpdateStatus = true;
                        }
                    }
                }
            }
        }

        private String getCategoryName(int cat_id) {
            Cursor cursor;
            cursor = db.rawQuery("SELECT * FROM " + utilHelper.TB_CAT_NAME + " WHERE " + utilHelper.F_CAT_ID + " = " + cat_id, null);
            while (cursor.moveToNext()) {
                return cursor.getString(cursor.getColumnIndex(utilHelper.F_CAT_IMG));
            }
            cursor.close();
            return "no";
        }

        public interface getStatus {
            void status(Boolean status);
        }

        public interface getData {
            void getCategoryList(List<categoryModel> data);
        }
    }

    public static class sqliteExerciseAsync extends AsyncTask<Void, Void, List<exerciseModel>> {
        WeakReference<Context> context;
        sqliteHelper helper;
        getStatus Interfacestatus;
        getData Interfacedata;
        String functionName;
        List<exerciseModel> exeList;
        List<delete_exercise_or_cat_model> del_exe_list;
        SQLiteDatabase db;
        Boolean insertOrCheckStatus = false;
        int id;
        String total_exercise;

        //for update
        public sqliteExerciseAsync(Context context, String functionName, List<exerciseModel> exelist, getStatus Interfacestatus) {
            this.context = new WeakReference<>(context);
            this.Interfacestatus = Interfacestatus;
            this.functionName = functionName;
            this.exeList = exelist;
        }

        //for delete
        public sqliteExerciseAsync(Context context, List<delete_exercise_or_cat_model> del_exe_list, String functionName, getStatus Interfacestatus) {
            this.context = new WeakReference<>(context);
            this.Interfacestatus = Interfacestatus;
            this.functionName = functionName;
            this.del_exe_list = del_exe_list;
        }

        //for getdata
        public sqliteExerciseAsync(Context context, int id, String functionName, getData Interfacedata) {
            this.context = new WeakReference<>(context);
            this.Interfacedata = Interfacedata;
            this.functionName = functionName;
            this.id = id;
            exeList = new ArrayList<>();
        }

        //for insert
        public sqliteExerciseAsync(Context context, int id, String exercises, String functionName, getStatus Interfacestatus) {
            this.context = new WeakReference<>(context);
            this.Interfacestatus = Interfacestatus;
            this.functionName = functionName;
            this.id = id;
            this.total_exercise = exercises;
            exeList = new ArrayList<>();
        }

        @Override
        protected List<exerciseModel> doInBackground(Void... voids) {
            helper = new sqliteHelper(context.get());
            db = helper.getWritableDatabase();
            switch (functionName) {
                case utilHelper.FUN_INSERT_EXE:
                    insertExercise();
                    break;
                case utilHelper.FUN_GET_EXE:
                    getExerise(id);
                    break;
                case utilHelper.FUN_CHECK_EXE:
                    insertOrCheckStatus = isExeDataExists(id);
                    break;
                case utilHelper.FUN_DEL_EXE:
                    delExercise();
                    break;
            }
            return exeList;
        }


        @Override
        protected void onPostExecute(List<exerciseModel> exerciseModels) {
            if (Interfacestatus != null)
                Interfacestatus.status(insertOrCheckStatus);
            if (Interfacedata != null)
                Interfacedata.getExerciseList(exerciseModels);
            super.onPostExecute(exerciseModels);
        }

        private void insertExercise() {
            int newCat = 0, insCat = 0;
            if (!exeList.isEmpty()) {
                int size = exeList.size();
                Log.e("sqlite: exe list size", String.valueOf(size));
                for (int i = 0; i < size; i++) {
                    if (!isExeExists(exeList.get(i).getId())) {
                        //insert new exe
                        newCat++;
                        ContentValues cv = new ContentValues();
                        cv.put(utilHelper.F_EXE_ID, exeList.get(i).getId());
                        cv.put(utilHelper.F_EXE_TITLE, exeList.get(i).getExercise_title());
                        cv.put(utilHelper.F_EXE_IMAGE, exeList.get(i).getImage_name());
                        cv.put(utilHelper.F_EXE_CAL, exeList.get(i).getCal());
                        cv.put(utilHelper.F_EXE_DESC, exeList.get(i).getDescription());
                        cv.put(utilHelper.F_EXE_CAT_ID, exeList.get(i).getCat_id());

                        cv.put(utilHelper.F_EXE_SETS, exeList.get(i).getSet());
                        cv.put(utilHelper.F_EXE_TIME, exeList.get(i).getTime());
                        cv.put(utilHelper.F_EXE_REST_TIME, exeList.get(i).getRest_time());
                        long res = db.insert(utilHelper.TB_EXE_NAME, null, cv);
                        if (res != -1) {
                            insCat++;
                        }
                    } else {
                        //update exercise
                        ContentValues cv = new ContentValues();
                        cv.put(utilHelper.F_EXE_ID, exeList.get(i).getId());
                        cv.put(utilHelper.F_EXE_TITLE, exeList.get(i).getExercise_title());
                        cv.put(utilHelper.F_EXE_IMAGE, exeList.get(i).getImage_name());
                        cv.put(utilHelper.F_EXE_CAL, exeList.get(i).getCal());
                        cv.put(utilHelper.F_EXE_DESC, exeList.get(i).getDescription());
                        cv.put(utilHelper.F_EXE_CAT_ID, exeList.get(i).getCat_id());
                        cv.put(utilHelper.F_EXE_SETS, exeList.get(i).getSet());
                        cv.put(utilHelper.F_EXE_TIME, exeList.get(i).getTime());
                        cv.put(utilHelper.F_EXE_REST_TIME, exeList.get(i).getRest_time());
                        long res = db.update(utilHelper.TB_EXE_NAME, cv, utilHelper.F_EXE_ID + "= '" + exeList.get(i).getId() + "'", null);
                        if (res != -1)
                            insertOrCheckStatus = true;
                    }
                }
            }
            if (newCat != 0 && insCat != 0) {
                if (newCat == insCat) {
                    //dowanload images from server
                    int count = utilHelper.dowanloadGIFImagesOffline(context.get(), exeList);
                    Log.e("count", String.valueOf(count));
                    if (count == exeList.size())
                        insertOrCheckStatus = true;
                }
            }
            db.close();
        }


        private void delExercise() {
            if (!del_exe_list.isEmpty()) {
                int size = del_exe_list.size();
                Log.e("sqlite: exe list size", String.valueOf(size));
                for (int i = 0; i < size; i++) {
                    if (isExeExists(del_exe_list.get(i).getExercise())) {
                        String imgname = getExeImgName(Integer.parseInt(del_exe_list.get(i).getExercise()));
                        if (!imgname.equals("no")) {
                            utilHelper.deleteFileFromInternalStorage(context.get(), imgname, "gif");
                            Log.e("deleted gif", imgname);
                        }

                        long res = db.delete(utilHelper.TB_EXE_NAME, utilHelper.F_EXE_ID + "= '" + del_exe_list.get(i).getExercise() + "'", null);
                        if (res != -1) {
                            insertOrCheckStatus = true;
                        }
                    }
                }
            }
        }

        private boolean isExeExists(String exe_id) {
            Boolean status = false;
            Cursor cursor = db.rawQuery("SELECT * FROM " + utilHelper.TB_EXE_NAME + " WHERE " + utilHelper.F_EXE_ID + " = " + exe_id, null);
            if (cursor.moveToNext())
                status = true;
            cursor.close();
            return status;
        }

        private void getExerise(int cat_id) {
            Cursor cursor = db.rawQuery("SELECT * FROM " + utilHelper.TB_EXE_NAME + " WHERE " + utilHelper.F_EXE_CAT_ID + " = " + cat_id, null);
            while (cursor.moveToNext()) {
                exerciseModel model = new exerciseModel();
                model.setId(cursor.getString(cursor.getColumnIndex(utilHelper.F_EXE_ID)));
                model.setExercise_title(cursor.getString(cursor.getColumnIndex(utilHelper.F_EXE_TITLE)));
                model.setImage_name(cursor.getString(cursor.getColumnIndex(utilHelper.F_EXE_IMAGE)));
                model.setCal(cursor.getString(cursor.getColumnIndex(utilHelper.F_EXE_CAL)));
                model.setCat_id(cursor.getString(cursor.getColumnIndex(utilHelper.F_EXE_CAT_ID)));
                model.setDescription(cursor.getString(cursor.getColumnIndex(utilHelper.F_EXE_DESC)));

                model.setSet(cursor.getString(cursor.getColumnIndex(utilHelper.F_EXE_SETS)).toLowerCase());
                model.setTime(cursor.getString(cursor.getColumnIndex(utilHelper.F_EXE_TIME)));
                model.setRest_time(cursor.getString(cursor.getColumnIndex(utilHelper.F_EXE_REST_TIME)));
                exeList.add(model);
            }
            cursor.close();
            db.close();
        }

        private String getExeImgName(int exe_id) {
            Cursor cursor = db.rawQuery("SELECT * FROM " + utilHelper.TB_EXE_NAME + " WHERE " + utilHelper.F_EXE_ID + "= " + exe_id, null);
            while (cursor.moveToNext()) {

                return cursor.getString(cursor.getColumnIndex(utilHelper.F_EXE_IMAGE));
            }
            cursor.close();
            return "no";
        }

        Boolean isExeDataExists(int cat_id) {
            Boolean status = false;
            Cursor cursor = db.rawQuery("SELECT * FROM " + utilHelper.TB_EXE_NAME + " WHERE " + utilHelper.F_EXE_CAT_ID + " = " + cat_id, null);
            if (cursor.moveToNext()) {
//                if (cursor.getCount() == Integer.valueOf(total_exercise)) {
                status = true;
//                }
            }
            cursor.close();
            return status;
        }

        public interface getStatus {
            void status(Boolean status);
        }

        public interface getData {
            void getExerciseList(List<exerciseModel> data);
        }
    }

    public static class sqliteWeightTrackerAsync extends AsyncTask<Void, Void, List<weightTrackerModel>> {
        WeakReference<Context> context;
        sqliteHelper helper;
        getStatus Interfacestatus;
        getData Interfacedata;
        String functionName;
        List<weightTrackerModel> weightList;
        SQLiteDatabase db;
        String insertOrUpdateStatus = "false";
        String activeTab;

        //for update,insert
        public sqliteWeightTrackerAsync(Context context, String functionName, List<weightTrackerModel> weightList, getStatus Interfacestatus) {
            this.context = new WeakReference<>(context);
            this.Interfacestatus = Interfacestatus;
            this.functionName = functionName;
            this.weightList = weightList;
        }

        public sqliteWeightTrackerAsync(Context context, String functionName, getStatus Interfacestatus) {
            this.context = new WeakReference<>(context);
            this.Interfacestatus = Interfacestatus;
            this.functionName = functionName;
        }

        //for getdata

        public sqliteWeightTrackerAsync(Context context, String activeTab, String functionName, getData Interfacedata) {
            this.context = new WeakReference<>(context);
            this.Interfacedata = Interfacedata;
            this.functionName = functionName;
            this.activeTab = activeTab;
            weightList = new ArrayList<>();
        }

        @Override
        protected List<weightTrackerModel> doInBackground(Void... voids) {
            helper = new sqliteHelper(context.get());
            db = helper.getWritableDatabase();
            switch (functionName) {
                case utilHelper.FUN_INSERT_WEIGHT:
                    insertExercise();
                    break;
                case utilHelper.FUN_GET_WEIGHT:
                    getExerise(activeTab);
                    break;
                case utilHelper.FUN_UPDATE_WEIGHT:
                    updateWeight();
                    break;
                case utilHelper.FUN_DEL_WEIGHT:
                    deleteData();
                    break;
            }
            return weightList;
        }

        @Override
        protected void onPostExecute(List<weightTrackerModel> exerciseModels) {
            if (Interfacestatus != null)
                Interfacestatus.status(insertOrUpdateStatus);
            if (Interfacedata != null)
                Interfacedata.getWeightList(exerciseModels);
            super.onPostExecute(exerciseModels);
        }

        private void insertExercise() {
            if (!weightList.isEmpty()) {
                int size = weightList.size();
                Log.e("sqlite:weight list size", String.valueOf(size));
                for (int i = 0; i < size; i++) {
                    if (!isExeExists(weightList.get(i).getFull_date())) {
                        ContentValues cv = new ContentValues();
                        cv.put(utilHelper.F_WT_WEIGHT, weightList.get(i).getWeight());
                        cv.put(utilHelper.F_WT_FULL_DATE, weightList.get(i).getFull_date());
                        cv.put(utilHelper.F_WT_DAY, weightList.get(i).getDay());
                        cv.put(utilHelper.F_WT_MONTH, weightList.get(i).getMoth());
                        cv.put(utilHelper.F_WT_YEAR, weightList.get(i).getYear());
                        long res = db.insert(utilHelper.TB_WEIGHT_TRACKER_NAME, null, cv);
                        if (res != -1) {
                            insertOrUpdateStatus = "true";
                        }
                    } else {
                        insertOrUpdateStatus = "exist";
                    }
                }
            }
            db.close();
        }

        private boolean isExeExists(String date) {
            Boolean status = false;
            Cursor cursor = db.rawQuery("SELECT * FROM " + utilHelper.TB_WEIGHT_TRACKER_NAME + " WHERE " + utilHelper.F_WT_FULL_DATE + " = '" + date + "'", null);
            if (cursor.moveToNext())
                status = true;
            cursor.close();
            return status;
        }

        private void getExerise(String activeTab) {
            Cursor cursor = null;
            if (activeTab.equals(context.get().getResources().getString(R.string.tab_title_Week)))
                cursor = db.rawQuery("SELECT * FROM " + utilHelper.TB_WEIGHT_TRACKER_NAME + " order by " + utilHelper.F_WT_FULL_DATE + " desc limit 7", null);
            else if (activeTab.equals(context.get().getResources().getString(R.string.tab_title_month)))
                cursor = db.rawQuery("SELECT * FROM " + utilHelper.TB_WEIGHT_TRACKER_NAME + " order by " + utilHelper.F_WT_FULL_DATE + " desc limit 30", null);
            else if (activeTab.equals(context.get().getResources().getString(R.string.tab_title_year)))
                cursor = db.rawQuery("SELECT id,year,month,day,full_date,avg(weight) as weight FROM " + utilHelper.TB_WEIGHT_TRACKER_NAME + " group by year order by " + utilHelper.F_WT_FULL_DATE + " desc", null);

            while (cursor.moveToNext()) {
                weightTrackerModel model = new weightTrackerModel();
                model.setWeight(cursor.getString(cursor.getColumnIndex(utilHelper.F_WT_WEIGHT)));
                model.setFull_date(cursor.getString(cursor.getColumnIndex(utilHelper.F_WT_FULL_DATE)));
                model.setDay(cursor.getString(cursor.getColumnIndex(utilHelper.F_WT_DAY)));
                model.setMoth(cursor.getString(cursor.getColumnIndex(utilHelper.F_WT_MONTH)));
                model.setYear(cursor.getString(cursor.getColumnIndex(utilHelper.F_WT_YEAR)));
                weightList.add(model);
            }
            cursor.close();
            db.close();
        }

        private void updateWeight() {
            String date = weightList.get(0).getFull_date();
            Log.e("date", date);
            String weight = weightList.get(0).getWeight();
            Log.e("we", weight);
            ContentValues cv = new ContentValues();
            cv.put(utilHelper.F_WT_WEIGHT, weight);
            long res = db.update(utilHelper.TB_WEIGHT_TRACKER_NAME, cv, utilHelper.F_WT_FULL_DATE + "= '" + date + "'", null);
            if (res != -1)
                insertOrUpdateStatus = "true";
            db.close();
        }

        private void deleteData() {
            long res = db.delete(utilHelper.TB_WEIGHT_TRACKER_NAME, null, null);
            if (res != -1) {
                insertOrUpdateStatus = "true";
            }
            db.close();
        }

        public interface getStatus {
            void status(String status);
        }

        public interface getData {
            void getWeightList(List<weightTrackerModel> data);
        }
    }

    public static class sqliteDailyJournalAsync extends AsyncTask<Void, Void, List<dailyJournalModel>> {
        WeakReference<Context> context;
        sqliteHelper helper;
        getStatus Interfacestatus;
        getData Interfacedata;
        String functionName;
        List<dailyJournalModel> List;
        List<dailyJournalDetailModel> DetailList;
        SQLiteDatabase db;
        String insertOrUpdateStatus = "false";
        String date;

        //for update,insert
        public sqliteDailyJournalAsync(Context context, String functionName, List<dailyJournalDetailModel> List, getStatus Interfacestatus) {
            this.context = new WeakReference<>(context);
            this.Interfacestatus = Interfacestatus;
            this.functionName = functionName;
            this.DetailList = List;
        }

        //for delete
        public sqliteDailyJournalAsync(Context context, String functionName, getStatus Interfacestatus) {
            this.context = new WeakReference<>(context);
            this.Interfacestatus = Interfacestatus;
            this.functionName = functionName;
        }

        //for getdata
        public sqliteDailyJournalAsync(Context context, String date, String functionName, getData Interfacedata) {
            this.context = new WeakReference<>(context);
            this.Interfacedata = Interfacedata;
            this.functionName = functionName;
            this.date = date;
            List = new ArrayList<>();
        }

        @Override
        protected List<dailyJournalModel> doInBackground(Void... voids) {
            helper = new sqliteHelper(context.get());
            db = helper.getWritableDatabase();
            switch (functionName) {
                case utilHelper.FUN_INSERT_JOURNAL:
                    insertData();
                    break;
                case utilHelper.FUN_GET_JOURNAL:
                    getData();
                    break;
                case utilHelper.FUN_UPDATE_JOURNAL:
                    updateData();
                    break;
                case utilHelper.FUN_DEL_DAILY_JOURNAL_DETAIL:
                    deleteData();
                    break;
            }
            return List;
        }

        @Override
        protected void onPostExecute(List<dailyJournalModel> exerciseModels) {
            if (Interfacestatus != null)
                Interfacestatus.status(insertOrUpdateStatus);
            if (Interfacedata != null)
                Interfacedata.getDailyJournalList(exerciseModels);
            super.onPostExecute(exerciseModels);
        }

        private void insertData() {
            if (DetailList != null) {
                int size = DetailList.size();
                for (int i = 0; i < size; i++) {
                    ContentValues cv = new ContentValues();
                    cv.put(utilHelper.F_J_D_JID, DetailList.get(i).getJournalId());
                    cv.put(utilHelper.F_J_D_TITLE, DetailList.get(i).getFirst_element());
                    cv.put(utilHelper.F_J_D_DATE, DetailList.get(i).getDate());
                    cv.put(utilHelper.F_J_D_CAL, DetailList.get(i).getSecond_element());
                    long res = db.insert(utilHelper.TB_JOURNAL_DETAIL, null, cv);
                    if (res != -1) {
                        insertOrUpdateStatus = "true";
                    }
                }
                db.close();
            }
        }


        private void getData() {
            Cursor cursor;
            cursor = db.rawQuery("SELECT * FROM " + utilHelper.TB_JOURNAL, null);
            while (cursor.moveToNext()) {
                dailyJournalModel model = new dailyJournalModel();
                model.setId(cursor.getInt(cursor.getColumnIndex(utilHelper.F_J_ID)));
                model.setTitle(cursor.getString(cursor.getColumnIndex(utilHelper.F_J_TITLE)));
                List<dailyJournalDetailModel> detailList = getJournalDetail(cursor.getInt(cursor.getColumnIndex(utilHelper.F_J_ID)), cursor.getString(cursor.getColumnIndex(utilHelper.F_J_TITLE)));
                model.setDetailList(detailList);
                List.add(model);
            }
            cursor.close();
            db.close();
        }

        private List<dailyJournalDetailModel> getJournalDetail(int id, String Type) {
            List<dailyJournalDetailModel> detailModelList = new ArrayList<>();
            Cursor cursor = db.rawQuery("SELECT * FROM " + utilHelper.TB_JOURNAL_DETAIL + " WHERE " + utilHelper.F_J_D_DATE + " = '" + date + "' and " + utilHelper.F_J_D_JID + "=" + id, null);
            while (cursor.moveToNext()) {
                dailyJournalDetailModel model = new dailyJournalDetailModel();
                model.setId(cursor.getInt(cursor.getColumnIndex(utilHelper.F_J_D_ID)));
                model.setJournalId(cursor.getInt(cursor.getColumnIndex(utilHelper.F_J_D_JID)));
                model.setFirst_element(cursor.getString(cursor.getColumnIndex(utilHelper.F_J_D_TITLE)));
                model.setSecond_element(cursor.getString(cursor.getColumnIndex(utilHelper.F_J_D_CAL)));
                model.setDate(cursor.getString(cursor.getColumnIndex(utilHelper.F_J_D_DATE)));
                model.setType(Type);
                detailModelList.add(model);
            }
            cursor.close();
            return detailModelList;
        }


        private void updateData() {
            ContentValues cv = new ContentValues();
            long res;
            if (!isDataExist(DetailList.get(0).getJournalId())) {
                cv.put(utilHelper.F_J_D_JID, DetailList.get(0).getJournalId());
                cv.put(utilHelper.F_J_D_TITLE, DetailList.get(0).getFirst_element());
                cv.put(utilHelper.F_J_D_DATE, DetailList.get(0).getDate());
                cv.put(utilHelper.F_J_D_CAL, DetailList.get(0).getSecond_element());
                res = db.insert(utilHelper.TB_JOURNAL_DETAIL, null, cv);
            } else {
                //title means no of glasses or weight or how i feel today
                cv.put(utilHelper.F_J_D_TITLE, DetailList.get(0).getFirst_element());
                res = db.update(utilHelper.TB_JOURNAL_DETAIL, cv, utilHelper.F_J_D_DATE + " = '" + DetailList.get(0).getDate() + "' and " + utilHelper.F_J_D_JID + "=" + DetailList.get(0).getJournalId(), null);
            }
            if (res != -1)
                insertOrUpdateStatus = "true";
            db.close();
        }

        private boolean isDataExist(int id) {
            boolean isExist = false;
            Cursor cursor = db.rawQuery("SELECT * FROM " + utilHelper.TB_JOURNAL_DETAIL + " WHERE " + utilHelper.F_J_D_DATE + " = '" + DetailList.get(0).getDate() + "' and " + utilHelper.F_J_D_JID + "=" + id, null);
            if (cursor.moveToNext())
                isExist = true;
            cursor.close();
            return isExist;
        }

        private void deleteData() {
            long res = db.delete(utilHelper.TB_JOURNAL_DETAIL, null, null);
            if (res != -1) {
                insertOrUpdateStatus = "true";
            }
            db.close();
        }

        public interface getStatus {
            void status(String status);
        }

        public interface getData {
            void getDailyJournalList(List<dailyJournalModel> data);
        }
    }
*/
}
