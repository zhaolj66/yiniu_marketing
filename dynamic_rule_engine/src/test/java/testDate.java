import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class testDate {
    public static void main(String[] args) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date old_apply_dt = simpleDateFormat.parse("2021-12-01");
        Calendar instance = Calendar.getInstance();
        instance.setTime(old_apply_dt);
        instance.add(Calendar.DAY_OF_MONTH,-1);
        Date time = instance.getTime();
        String new_apply_dt = simpleDateFormat.format(time);
        System.out.println(new_apply_dt);
    }
}
