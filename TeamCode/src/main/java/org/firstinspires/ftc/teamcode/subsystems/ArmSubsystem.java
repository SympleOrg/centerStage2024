package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.command.SubsystemBase;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.util.FeedForward;
import org.firstinspires.ftc.teamcode.util.MathUtil;
import org.firstinspires.ftc.teamcode.util.Motors;

@Config
public class ArmSubsystem extends SubsystemBase {
    private final MotorEx motor;
    private final JointSubSystem jointSubSystem;
    private final FeedForward feedForward;
    public static final double STARTING_DEG = -33.75;
    public static double KG = 0.27;
    public static double GravitationalZeroDeg = 90;

    public ArmSubsystem(HardwareMap hMap, JointSubSystem jointSubSystem) {
        this.feedForward = new FeedForward(KG, STARTING_DEG);
        this.jointSubSystem = jointSubSystem;

        this.motor = new MotorEx(hMap, Motors.ARM.id);
        this.motor.resetEncoder();

        this.motor.setInverted(true);
    }

    public void moveMotor(double power, boolean feedForward) {
        this.motor.setRunMode(Motor.RunMode.RawPower);

        double motorPower = power + (feedForward ? this.calcCurrentFeedforward() : 0);
        this.motor.set(motorPower);
    }

    public double calcCurrentFeedforward() {
        return this.feedForward.calc(getCurrentPositionDeg(), GravitationalZeroDeg);
    }

    public void moveMotor(double power) {
        moveMotor(power, true);
    }

    public double getCurrentPositionDeg() {
        return this.feedForward.convertRelativeDegToAbsolute(MathUtil.countsToDeg(this.motor.getCurrentPosition(), Motors.ARM.ticksPerRev));
    }

    public double getSpeed() {
        return this.motor.get();
    }

    public void resetPos() {
        this.motor.resetEncoder();
    }

    public enum ArmPositions {
        TAKE(STARTING_DEG-15),
        PLACE(145),
        FOLD(STARTING_DEG-15),
        HOOK(90);

        public final double deg;

        ArmPositions(double deg) {
            this.deg = deg;
        }
    }
}
