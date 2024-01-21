package org.firstinspires.ftc.teamcode.subsystems;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.util.FeedForward;
import org.firstinspires.ftc.teamcode.util.MathUtil;

public class ArmSubsystem extends SubsystemBase {
    private final int ARM_MOTOR_COUNTS = 288;

    private final MotorEx motor;
    private final JointSubSystem jointSubSystem;
    private final FeedForward feedForward;
    private static final double STARTING_DEG = -33.75;
    public static double KClawJoint = 1.2;

    public ArmSubsystem(HardwareMap hMap, JointSubSystem jointSubSystem) {
        this.feedForward = new FeedForward(0.3, STARTING_DEG);
        this.jointSubSystem = jointSubSystem;

        this.motor = new MotorEx(hMap, "arm_motor");
        this.motor.resetEncoder();

        this.motor.setInverted(true);
    }

    public void moveMotor(double power, boolean feedForward) {
//        telemetry.addData("JOYSTICK POWER: ", String.valueOf(power));
        this.motor.setRunMode(Motor.RunMode.RawPower);

        double motorPower = power + (feedForward ? this.calcCurrentFeedforward() : 0);
//        telemetry.addData("POWER:", motorPower);
//        telemetry.addData("FEED FORWARD POWER:", feedForwardPower);
        this.motor.set(motorPower);
    }

    private double calcCurrentFeedforward() {
        double clawJointDeg = this.jointSubSystem.getClawJointAngle();
        double calcClawJointPower = Math.abs(Math.cos(Math.toRadians(clawJointDeg))) * KClawJoint * -1;

        double feedForwardPower = this.feedForward.calc(getCurrentPositionDeg()) + (getCurrentPositionDeg() > 90 ? calcClawJointPower : 0);

        return feedForwardPower;
    }

    public void moveMotor(double power) {
        moveMotor(power, true);
    }

    public double getCurrentPositionDeg() {
        return this.feedForward.convertRelativeDegToAbsolute(MathUtil.countsToDeg(this.motor.getCurrentPosition(), ARM_MOTOR_COUNTS));
    }

    public enum ArmPositions {
        TAKE(STARTING_DEG),
        PLACE(120);

        public final double deg;

        ArmPositions(double deg) {
            this.deg = deg;
        }
    }
}
