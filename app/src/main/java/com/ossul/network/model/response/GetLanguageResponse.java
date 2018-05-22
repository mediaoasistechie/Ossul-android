package com.ossul.network.model.response;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetLanguageResponse extends BaseResponse {
    @Expose
    @SerializedName("data")
    public JsonObject data;


    public GetLanguageResponse() {

    }


    public static GetLanguageResponse fromJson(String json) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, GetLanguageResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String emptyJson() {
        return "{}";
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
/*
{
  "success": true,
  "data": {
    "Ok": "موافق",
    "Done": "تم",
    "No": "لا",
    "Yes": "نعم",
    "Cancel": "إلغاء",
    "Total": "المجموع",
    "Properties": "العقارات",
    "By": "بواسطة",
    "Edit": "تعديل",
    "View Offer": "مشاهدة العرض",
    "To": "إلى",
    "From": "من",
    "Username": "اسم المستخدم",
    "Password": "كلمة المرور",
    "Sign in": "تسجيل الدخول",
    "Don't have an account?": "ليس لديك حساب ؟",
    "Sign Up": "تسجيل",
    "Forgot Password?": "نسيت كلمة المرور؟",
    "Click here": "اضغط هنا",
    "Please enter Email": "فضلاَ ادخل البريد الإلكتروني",
    "Please enter Password": "فضلاَ أدخل كلمة المرور",
    "Please enter valid email id": "فضلاَ ادخل بريد إلكتروني صحيح",
    "Please Enter your Name": "فضلاَ ادخل اسمك",
    "Please Enter Country code": "Please Enter Country code",
    "Please Enter Mobile Number": "فضلاَ ادخل رقم الجوال",
    "Camera": "كاميرا",
    "Album": "ألبوم",
    "Name": "الاسم",
    "Email": "البريد الإلكتروني",
    "Area Code": "Area Code",
    "Mobile Number": "رقم الجوال",
    "Register": "سجل",
    "Please enter your email then click submit": "فضلا ادخل بريدك الإلكتروني ثم اضغط موافق ",
    "Your email": "بريدك الإلكتروني",
    "Submit": "تقديم",
    "Search": "بحث",
    "Would you like to logout?": "Would you like to logout?",
    "My eCatalogue": "كتالوجي",
    "Please enter eCatalogue name": "فضلاَ أدخل اسم الكتالوج",
    "Real estate offices": "المكاتب العقارية",
    "Manage eCatalogues": "إدارة الكتالوجات",
    "Received eCatalogue": "إستقبال الكتالوجات",
    "Submit request": "تقديم الطلب",
    "My request": "طلباتي",
    "My offers": "عروضي",
    "Profile": "الملف الشخصي",
    "Change Password": "تغير كلمة المرور",
    "Logout": "تسجيل الخروج",
    "My Profile": "ملفي الشخصي",
    "Following": "متابع",
    "Offers": "عروض",
    "Requests": "طلبات",
    "Edit My Profile": "تعديل ملفي الشخصي",
    "Please enter Area code": "Please enter Area code",
    "Manage Account": "Manage Account",
    "eCatalogues": "eCatalogues",
    "Please enter Confirm password": "Please enter Confirm password",
    "Password and Confirm password should be same": "يجب أن تكون كلمة المرور وتأكيد كلمة المرور متطابقة",
    "Total Properties": "مجموع العقارات",
    "You are about to make a call. Do you want to continue?": "انت على وشك إجراء مكالمة. هل تريد إجراء مكالمة",
    "Call": "مكالمة",
    "Directions": "Directions",
    "Price": "السعر",
    "Delete": "Delete",
    "Submit Property in eCatalogue": "حفظ العقار في الكتالوج",
    "Please Add Title": "فضلاَ أضف عنوان",
    "Please Add Location": "فضلاَ أضف الموقع",
    "Please enter Neighborhood": "فضلاَ أدخل الحي",
    "Please select City": "فضلاَ اختر المدينة",
    "Please select Real Estate office": "فضلاَ اختر مكتب العقار",
    "Please select Region": "فضلاَ اختر المنطقة",
    "Please select Proper type": "فضلاَ اختر نوع العقار",
    "Please select Category": "فضلاَ اختر الفئة",
    "Please select Offer type": "فضلاَ اختر نوع العرض",
    "Please enter Price": "فضلاَ ادخل السعر",
    "Please enter Description": "فضلاَ ادخل الوصف",
    "Browse Property": "استعراض العقار",
    "Search Properties": "بحث العقارات",
    "Share eCatalogue": "مشاركة الكتالوج",
    "Invite": "دعوة",
    "Send": "إرسال",
    "Please select atleast one user.": "فضلاَ اختيار عضو واحد على الإقل",
    "Edit Request": "Edit Request",
    "Submit Request": "تقديم الطلب",
    "Describe what you need": "اوصف ماذا تريد",
    "Select country": "إختر الدولة",
    "Select city": "إختر المدينة",
    "For Rent/Lease": "لل إيجار / إيجار تمويلي",
    "Select property type": "إختر نوع العقار",
    "Enter minimum price": "ادخل أقل سعر",
    "Enter maximum price": "ادخل أعلى سعر",
    "Enter description": "ادخل وصف ",
    "Select a zone": "Select a zone",
    "My Request": "طلباتي",
    "Search Request": "بحث الطلبات",
    "Available Request": "الطلبات المتوفرة",
    "Describe your offer here...": "اوصف عرضك هنا.....",
    "Enter your offer details.": "ادخل تفاصيل عرضك",
    "Received Offer": "Received Offer",
    "Sent Offer": "إرسال عرض",
    "Search Offers": "بحث العروض",
    "My Offers": "عروضي",
    "Received Offers": "إستقبال العروض",
    "Select Office": "إختر المكتب",
    "Select Category": "إختر الفئة",
    "Contact Office": "إتصل بالمكتب",
    "Follow": "متابعة",
    "Offices": "مكاتب",
    "al Oula Builders": "الأولى",
    "Al Showeir": "الشويعر",
    "Al Rajhi Investment": "الراجحي للإستثمار",
    "Offer": "العرض",
    "E-Catalogues": "كتالوجات",
    "Specials": "العروض الخاصة",
    "Sent Offers": "إرسال عروض",
    "Repeat Password": "اعد كلمة المرور",
    "Save Password": "حفظ كلمة المرو",
    "Your Password update successfully": "تم تحديث كلمة المرور بنجاح",
    "Edit Profile": "تعديل الملف الشخصي",
    "Location": "Location",
    "Submit Offer": "تقديم العرض",
    "For Rent eCatalogue": "كتالوج للإيجار",
    "For Sale eCatalogue": "كتالوج للبيع",
    "For Investment eCatalogue": "كتالوج للإستثمار",
    "Create New eCatalogue": "إنشاء كتالوج جديد",
    "Give a name to your new eCatalogue then press Ok": "اضف اسم للكتالوج الجديد ثم اضغط موافق",
    "Create": "إنشاء",
    "eCatalogue Name": "اسم الكتالوج",
    "Add Property in ecatalogue & share with your contact": "إضافة العقار في الكتالوج & ومشاركته",
    "Add Title": "أضف عنوان",
    "Add Location": "أضف موقع",
    "Neighbourhood ex:Airport": "حي ... مثال : المطار",
    "City": "المدينة",
    "Real Estate Office": "مكتب عقاري",
    "Region": "المنطقة",
    "Property Type": "نوع العقار",
    "Category": "الفئة",
    "Offer Type": "نوع العرض",
    "No. of Bedrooms": "عدد غرف النوم",
    "No. of Bathrooms": "عدد دوراة المياه",
    "Description": "وصف",
    "Country": "الدولة",
    "Neighbourhood": "حي",
    "Categories": "فئات",
    "Add to eCatalogue": "إضافة للكتالوج",
    "Submission Confirmed": "تأكيد الحفظ",
    "Your Property has been submit successfully": "تم تقديم عقارك بنجاح",
    "Submit One More ?": "تقديم واحد اخر ؟",
    "Send Successfully": "تم الارسال بنجاح",
    "Your eCatalogue has been shared successfully": "تم بنجاح مشاركة الكتالوج",
    "alOula Builders": "الأولى",
    "North": "شمال",
    "South": "جنوب",
    "East": "شرق",
    "West": "غرب",
    "Central": "الوسط",
    "Plot": "قطعة",
    "Building": "مبنى",
    "Apartment": "شقة",
    "Office": "مكتب",
    "Villa": "فيلا",
    "Warehouse": "مستودع",
    "Shops": "محل تجاري",
    "Hotel": "فندق",
    "Others": "أخرى",
    "For Rent": "للإيجار",
    "For Sale": "للبيع",
    "Username does not exists": "اسم المستخدم غير موجود",
    "Invalid password": "كلمة المرور غير صحيحة",
    "please enter (*) mandatory fields : This email does not exist": "الرجاء إدخال (*) حقول إلزامية: لا يوجد هذا البريد الإلكتروني",
    "Get Started": "إبداء",
    "Make Offer": "تقديم عرض",
    "Zone": "المنطقة",
    "Investment": "استثمار",
    "New Zone": "منطقة جديدة",
    "All Direction": "جميع الإتجاهات",
    "Price Range": "نطاق السعر",
    "Min:": "أقل:",
    "Max:": "أعلى:",
    "For sale/rent": "للـ بيع/ إيجار",
    "Maximum price should be greater than minimum price": "يجب أن يكون أقصى سعر أكبر من أقل سعر"
  }
}

 */