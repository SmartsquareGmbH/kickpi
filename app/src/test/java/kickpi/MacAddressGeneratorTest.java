package kickpi;

import android.app.Activity;
import android.provider.Settings;
import org.junit.*;
import de.smartsquare.kickpi.MacAddressGenerator;
import de.smartsquare.kickpi.MainActivity;

public class IDGeneratorTest {

    @Test
    public void that_generated_id_is_mac_address() {
        MacAddressGenerator macAddressGenerator = new MacAddressGenerator(new MainActivity());

        System.out.println(macAddressGenerator.generate());

        //assertThat(generatedID).isEqualTo(macAdress);
    }

}

