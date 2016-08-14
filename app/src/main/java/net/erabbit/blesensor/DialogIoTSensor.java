package net.erabbit.blesensor;

import android.bluetooth.BluetoothDevice;

import net.erabbit.bluetooth.BleDevice;
import net.erabbit.common_lib.CoolUtility;

import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by Tom on 16/7/20.
 */
public class DialogIoTSensor extends BleDevice {

    public enum SensorFeature {
        ACCELEROMETER(  0, "2ea78970-7d44-44bb-b097-26183f402401", 3, "g", 2),
        GYROSCOPE(      1, "2ea78970-7d44-44bb-b097-26183f402402", 3, "deg/s", 2),
        MAGNETOMETER(   2, "2ea78970-7d44-44bb-b097-26183f402403", 3, "uT", 0),
        BAROMETER(      3, "2ea78970-7d44-44bb-b097-26183f402404", 1, "Pa", 0),//Pressure
        HUMIDITY(       5, "2ea78970-7d44-44bb-b097-26183f402405", 1, "%", 0),
        TEMPERATURE(    4, "2ea78970-7d44-44bb-b097-26183f402406", 1, "°C", 2),
        SFL(            6, "2ea78970-7d44-44bb-b097-26183f402407", 4, "", 0);

        private UUID uuid;
        private int keyOffset;
        private int dimension;
        private String unit;
        private int precision;

        private float rangeMin = 0;
        private float rangeMax = 0;

        public UUID getUuid() {
            return uuid;
        }

        public int getKeyOffset() {
            return keyOffset;
        }

        public int getDimension() {
            return dimension;
        }

        public float[] getValueRange() {
            return new float[]{rangeMin, rangeMax};
        }

        SensorFeature(int keyOffset, String uuidString, int dimension, String unit, int precision) {
            this.uuid = UUID.fromString(uuidString);
            this.keyOffset = keyOffset;
            this.dimension = dimension;
            this.unit = unit;
            this.precision = precision;
        }

//        public static SensorFeature findByUUID(UUID uuid) {
//            for (SensorFeature feature:
//                 SensorFeature.values()) {
//                if(uuid.equals(feature.getUuid()))
//                    return feature;
//            }
//            return null;
//        }

        private boolean valueParsed = false;

        static final int maxValueDimension = 4;

        private float[] values = new float[maxValueDimension];

        public float[] getValues() {
            return values;
        }

        public boolean parseValue(byte[] data, Settings settings) {
            switch(this) {
                case ACCELEROMETER://in g
//                    var ax = (evothings.util.littleEndianToInt16(data, 3) / sensitvity).toFixed(2);
//                    var ay = (evothings.util.littleEndianToInt16(data, 5) / sensitvity).toFixed(2);
//                    var az = (evothings.util.littleEndianToInt16(data, 7) / sensitvity).toFixed(2);
                    for(int i = 0; i< dimension; i++)
                        values[i] = (short) CoolUtility.toIntLE(data, 3+2*i, 2) / (float)settings.accelerometerRange.getSensitivity();
                    break;
                case GYROSCOPE://in deg/s
//                    var ax = (evothings.util.littleEndianToInt16(data, 3) / sensitvity).toFixed(2);
//                    var ay = (evothings.util.littleEndianToInt16(data, 5) / sensitvity).toFixed(2);
//                    var az = (evothings.util.littleEndianToInt16(data, 7) / sensitvity).toFixed(2);
                    for(int i = 0; i< dimension; i++)
                        values[i] = (short) CoolUtility.toIntLE(data, 3+2*i, 2) / settings.gyroScopeRange.getSensitivity();
                    break;
                case MAGNETOMETER://in micro Tesla
//                    var ax = evothings.util.littleEndianToInt16(data, 3);
//                    var ay = evothings.util.littleEndianToInt16(data, 5);
//                    var az = evothings.util.littleEndianToInt16(data, 7);
                    for(int i = 0; i< dimension; i++)
                        values[i] = (short) CoolUtility.toIntLE(data, 3+2*i, 2);
                    break;
                case BAROMETER:
                    //var pressure = (evothings.util.littleEndianToUint32(data, 3) * (1/100)).toFixed(0);
                    values[0] = CoolUtility.toIntLE(data, 3, 4);//in Pascal
                    break;
                case HUMIDITY:
                    //var humidity = (evothings.util.littleEndianToUint32(data, 3) * (1/1024)).toFixed(0);
                    values[0] = CoolUtility.toIntLE(data, 3, 4) * (1f / 1024);//in %
                    break;
                case TEMPERATURE:
                    //var temperature = (evothings.util.littleEndianToUint32(data, 3) * 0.01).toFixed(2);
                    values[0] = CoolUtility.toIntLE(data, 3, 4) * 0.01f;//in degree celsius
                    break;
                case SFL:
//                    var wx = evothings.util.littleEndianToInt16(data, 3);
//                    var ax = evothings.util.littleEndianToInt16(data, 5);
//                    var ay = evothings.util.littleEndianToInt16(data, 7);
//                    var az = evothings.util.littleEndianToInt16(data, 9);
                    for(int i=0; i<dimension; i++)
                        values[i] = (short)CoolUtility.toIntLE(data, 3+2*i, 2);
                    break;
                default:
                    return false;
            }
            if(calibrating) {
                for(int i=0; i<dimension; i++) {
                    if(values[i] < minValues[i])
                        minValues[i] = values[i];
                    if(values[i] > maxValues[i])
                        maxValues[i] = values[i];
                }
            }
            valueParsed = true;
            return true;
        }

        public String getValueString() {
            if(!valueParsed)
                return "no value";
            String prefix = "";
            String valueString = null;
            if(dimension == 1)
                valueString = valueString(values[0]);
            else {
                if(dimension == 3)
                    prefix = "[x,y,z] = ";
                valueString = "[";
                for(int i = 0; i< dimension; i++) {
                    if(i > 0)
                        valueString += ", ";
                    valueString += valueString(values[i]);
                }
                valueString += "]";
            }
            if(valueString != null)
                return prefix + valueString + " " + unit;
            return null;
        }

        public String getValueString(float value) {
            return valueString(value) + " " + unit;
        }

        private String valueString(float value) {
            if(precision > 0)
                return String.format(Locale.getDefault(), "%." + String.valueOf(precision) + "f", value);
            else
                return String.format(Locale.getDefault(), "%d", (int)value);
        }

        protected boolean enabled = false;

        public boolean isEnabled() {
            return enabled;
        }

        float minValues[] = new float[maxValueDimension];
        float maxValues[] = new float[maxValueDimension];

        boolean calibrating = false;

        public void startCalibration() {
            calibrating = true;
            System.arraycopy(values, 0, minValues, 0, dimension);
            System.arraycopy(values, 0, maxValues, 0, dimension);
        }

        public void stopCalibration() {
            calibrating = false;
        }

        public float[] getCalibratedValues() {
            float calibratedValues[] = new float[dimension];
            for(int i=0; i<dimension; i++) {
                float calibratedValue = (values[i] - minValues[i]) / (maxValues[i] - minValues[i]);
                calibratedValues[i] = Math.min(Math.max(calibratedValue, 0f), 1f);
            }
            return calibratedValues;
        }
    }

    protected static final UUID UUID_INFO = UUID.fromString("2ea78970-7d44-44bb-b097-26183f402408"); // Read Device Features

    public DialogIoTSensor(BluetoothDevice device) {
        super(device);
        //服务和特性UUID
        UUID_MAIN_SERVICE = UUID.fromString("2ea78970-7d44-44bb-b097-26183f402400");
        UUID_MAIN_CONFIG = UUID.fromString("2ea78970-7d44-44bb-b097-26183f402409");//CONTROL_POINT
        UUID_MAIN_DATA = UUID.fromString("2ea78970-7d44-44bb-b097-26183f40240A");//CONTROL_REPLY
        //From BME280 datasheet
        SensorFeature.HUMIDITY.rangeMin = 0;
        SensorFeature.HUMIDITY.rangeMax = 100;
        SensorFeature.BAROMETER.rangeMin =  30000;
        SensorFeature.BAROMETER.rangeMax = 110000;
        SensorFeature.TEMPERATURE.rangeMin = -40;
        SensorFeature.TEMPERATURE.rangeMax = 85;
    }

    @Override
    public void onConnect() {
        startReceiveData();
        readInfo();
    }

    private void readInfo() {
        ReadCharacteristic(btGatt, btService, UUID_INFO);
    }

    private enum ControlCommand {
        ReadSettings(11), SensorOn(1), SensorOff(0);
        private byte id;
        public byte getId() {
            return id;
        }
        public static ControlCommand findById(byte commandId) {
            for(ControlCommand command : ControlCommand.values())
                if(command.getId() == commandId)
                    return command;
            return null;
        }
        ControlCommand(int commandId) {
            this.id = (byte)commandId;
        }
    }

    private void readSettings() {
        sendData(new byte[]{ControlCommand.ReadSettings.getId(), 0});
    }

    @Override
    protected void onReceiveData(byte[] data) {
        byte commandId = data[1];
        ControlCommand command = ControlCommand.findById(commandId);
        if(command != null)
            switch(command) {
                case ReadSettings:
                    settings.parse(data, 2);
                    break;
                case SensorOn:
                    sensorOn = true;
                    onValueChange(VALUE_OF_SENSOR_SWITCH, 1);
                    break;
                case SensorOff:
                    sensorOn = false;
                    onValueChange(VALUE_OF_SENSOR_SWITCH, 0);
                    break;
            }
    }

    private enum AccelerometerRange {
        _2G(3, 2), _4G(5, 4), _8G(8, 8), _16G(12, 16);
        private int key;
        private int value;
        public int getSensitivity() {
            return 32768 / value;
        }
        public static AccelerometerRange findByKey(int key) {
            for(AccelerometerRange range : AccelerometerRange.values())
                if(range.key == key)
                    return range;
            return null;
        }
        AccelerometerRange(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    private enum GyroscopeRange {
        _2000(0, 2000), _1000(1, 1000), _500(2, 500), _250(3, 250), _125(4, 125);
        private int key;
        private int value;
        public float getSensitivity() {
            return 32800f / value;
        }
        public static GyroscopeRange findByKey(int key) {
            for (GyroscopeRange range: GyroscopeRange.values()) {
                if(range.key == key)
                    return range;
            }
            return null;
        }
        GyroscopeRange(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

//    instance.configuration.BASIC = {
//                SENSOR_COMBINATION: 		instance.enums.SENSOR_COMBINATION._all,
//                ACCELEROMETER_RANGE: 		instance.enums.ACCELEROMETER_RANGE._2,
//                ACCELEROMETER_RATE: 		instance.enums.ACCELEROMETER_RATE._100,
//                GYROSCOPE_RANGE: 			instance.enums.GYROSCOPE_RANGE._2000,
//                GYROSCOPE_RATE: 			instance.enums.GYROSCOPE_RATE._100,
//                MAGNETOMETER_RATE: 			instance.enums.MAGNETOMETER_RATE._0,
//                ENVIRONMENTAL_SENSORS_RATE: instance.enums.ENVIRONMENTAL_SENSORS_RATE._2,
//                SENSOR_FUSION_RATE: 		instance.enums.SENSOR_FUSION_RATE._10,
//                SENSOR_FUSION_RAW_DATA_ENABLE: 	instance.enums.SENSOR_FUSION_RAW_DATA_ENABLE._enabled,
//                CALIBRATION_MODE: 			instance.enums.CALIBRATION_MODE._static,
//                AUTO_CALIBRATION_MODE: 		instance.enums.AUTO_CALIBRATION_MODE._basic,
//    }
    private class Settings {
        private AccelerometerRange accelerometerRange = AccelerometerRange._2G;
        private GyroscopeRange gyroScopeRange = GyroscopeRange._2000;
        public void parse(byte[] data, int offset) {
            accelerometerRange = AccelerometerRange.findByKey(data[offset+1]);
            gyroScopeRange = GyroscopeRange.findByKey(data[offset+3]);
        }
    }

    private Settings settings = new Settings();

    private void addFeature(SensorFeature feature) {
        if(!features.contains(feature))
            features.add(feature);
    }

    @Override
    protected void onReceiveData(UUID uuid, byte[] data) {
        super.onReceiveData(uuid, data);
        if(uuid.equals(UUID_INFO)) {
            for (SensorFeature feature:
                    SensorFeature.values()) {
                if(data[feature.getKeyOffset()] == 1) {
                    addFeature(feature);
                    if(feature == SensorFeature.SFL) {
                        addFeature(SensorFeature.ACCELEROMETER);
                        addFeature(SensorFeature.GYROSCOPE);
                        addFeature(SensorFeature.MAGNETOMETER);
                    }
                }
            }
            if(data.length > 7)
                firmwareVersion = new String(data, 7, data.length-7);
            super.onConnect();
        }
        else {
            //SensorFeature feature = SensorFeature.findByUUID(uuid);
            for(SensorFeature feature : features) {
                if(uuid.equals(feature.getUuid())) {
                    if(feature.parseValue(data, settings))
                        onValueChange(VALUE_OF_SENSOR_FEATURE, features.indexOf(feature));
                    break;
                }
            }
        }
    }

    public static final int VALUE_OF_SENSOR_SWITCH = 1;
    public static final int VALUE_OF_SENSOR_FEATURE = 2;

    protected ArrayList<SensorFeature> features = new ArrayList<>();

//    public SensorFeature[] getAvailableFeatures() {
//        SensorFeature[] availableFeatures = new SensorFeature[features.size()];
//        return features.toArray(availableFeatures);
//    }

    public int getFeatureCount() {
        return features.size();
    }

    public SensorFeature getFeature(int index) {
        return features.get(index);
    }

    private static final String FIRMWARE_VERSION_UNKNOWN = "unknown";
    private String firmwareVersion = FIRMWARE_VERSION_UNKNOWN;

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    private boolean sensorOn = false;

    public void switchSensorFeature(SensorFeature sensorFeature, boolean onOff) {
        if(onOff) {
            if(!sensorOn)
                switchSensor(true);
            EnableNotification(btGatt, btService, sensorFeature.getUuid());
        }
        else
            DisableNotification(btGatt, btService, sensorFeature.getUuid());
        sensorFeature.enabled = onOff;
    }

    public void switchSensor(boolean onOff) {
        sendData(new byte[]{onOff ? ControlCommand.SensorOn.getId() : ControlCommand.SensorOff.getId()});
    }
}
