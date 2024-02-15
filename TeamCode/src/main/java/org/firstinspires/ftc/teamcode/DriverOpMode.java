package org.firstinspires.ftc.teamcode;


import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.ParallelCommandGroup;
import com.arcrobotics.ftclib.command.button.Trigger;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.commands.arm.ArmByJoystickCommand;
import org.firstinspires.ftc.teamcode.commands.arm.GoToPickupPositionCommand;
import org.firstinspires.ftc.teamcode.commands.arm.MoveArmToPointWithPID;
import org.firstinspires.ftc.teamcode.commands.claw.CloseClawCommand;
import org.firstinspires.ftc.teamcode.commands.claw.OpenClawCommand;
import org.firstinspires.ftc.teamcode.commands.drivebase.ArcadeDriveCommand;
import org.firstinspires.ftc.teamcode.commands.drivebase.MoveUnderCommand;
import org.firstinspires.ftc.teamcode.commands.hook.AutoHookCloseCommand;
import org.firstinspires.ftc.teamcode.commands.hook.JoyStickHookCommand;
import org.firstinspires.ftc.teamcode.commands.hook.MoveToHookPositionCommand;
import org.firstinspires.ftc.teamcode.commands.joint.MoveJointToPosition;
import org.firstinspires.ftc.teamcode.subsystems.ArmSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.ClawSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.DriveBaseSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.HookSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.JointSubSystem;

@TeleOp(name="Driver Mode")
public class DriverOpMode extends CommandOpMode {
    private DriveBaseSubsystem driveBase;
    private ClawSubsystem clawSubsystem;
    private ArmSubsystem armSubsystem;
    private JointSubSystem jointSubsystem;
    private HookSubsystem hookSubsystem;

    private GamepadEx driverController;
    private GamepadEx actionController;

    @Override
    public void initialize() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        this.driverController = new GamepadEx(gamepad1);
        this.actionController = new GamepadEx(gamepad2);

        this.driveBase = new DriveBaseSubsystem(hardwareMap);
        this.clawSubsystem = new ClawSubsystem(hardwareMap);
        this.jointSubsystem = new JointSubSystem(hardwareMap);
        this.armSubsystem = new ArmSubsystem(hardwareMap, jointSubsystem);
        this.hookSubsystem = new HookSubsystem(hardwareMap);


        initDefaultCommands();
        initButtons();
    }

    @Override
    public void run() {
        super.run();
        telemetry.addData("ARM ANGLE", String.valueOf(this.armSubsystem.getCurrentPositionDeg()));
        telemetry.addData("CLAW JOINT ANGLE", String.valueOf(this.jointSubsystem.getClawJointAngle()));
        telemetry.addData("CLAW ANGLE", String.valueOf(this.clawSubsystem.getClawDeg()));
        telemetry.addData("Arm Command", String.valueOf(this.armSubsystem.getCurrentCommand().getName()));
        telemetry.addData("Arm FW", String.valueOf(this.armSubsystem.calcCurrentFeedforward()));
        telemetry.addData("Arm SPEED", String.valueOf(this.armSubsystem.getSpeed()));
        telemetry.update();
    }

    private void initDefaultCommands() {
        this.driveBase.setDefaultCommand(new ArcadeDriveCommand(this.driveBase, this.driverController));
        this.armSubsystem.setDefaultCommand(new ArmByJoystickCommand(this.armSubsystem, this.actionController));
        this.hookSubsystem.setDefaultCommand(new JoyStickHookCommand(this.hookSubsystem, this.actionController));
    }

    protected void initButtons() {
        this.driverController.getGamepadButton(GamepadKeys.Button.LEFT_BUMPER)
                .toggleWhenPressed(
                        new InstantCommand(() -> this.driveBase.changeSpeedModifier(DriveBaseSubsystem.SLOW_SPEED_MODIFIER)),
                        new InstantCommand(() -> this.driveBase.changeSpeedModifier(DriveBaseSubsystem.NORMAL_SPEED_MODIFIER))
                );

        this.driverController.getGamepadButton(GamepadKeys.Button.RIGHT_BUMPER)
                .toggleWhenPressed(
                        new InstantCommand(() -> this.driveBase.setInverted(true)),
                        new InstantCommand(() -> this.driveBase.setInverted(false))
                );

        this.driverController.getGamepadButton(GamepadKeys.Button.X)
                .whenHeld(new MoveUnderCommand(this.armSubsystem, this.driveBase, this.jointSubsystem));

//        this.driverController.getGamepadButton(GamepadKeys.Button.B)
//                .whenPressed(new InstantCommand(() -> this.armSubsystem.resetPos()));

        this.actionController.getGamepadButton(GamepadKeys.Button.RIGHT_BUMPER)
                .whenPressed(new ParallelCommandGroup(
                    new GoToPickupPositionCommand(this.armSubsystem),
                    new MoveJointToPosition(this.jointSubsystem, JointSubSystem.JointPositions.PICKUP)
                ));

        this.actionController.getGamepadButton(GamepadKeys.Button.LEFT_BUMPER)
                .whenPressed(new ParallelCommandGroup(
                    new MoveArmToPointWithPID(this.armSubsystem, ArmSubsystem.ArmPositions.PLACE),
                    new MoveJointToPosition(this.jointSubsystem, JointSubSystem.JointPositions.PUT)
                ));

        this.actionController.getGamepadButton(GamepadKeys.Button.A)
                .toggleWhenPressed(
                        new CloseClawCommand(this.clawSubsystem),
                        new OpenClawCommand(this.clawSubsystem)
                );

        this.actionController.getGamepadButton(GamepadKeys.Button.Y)
                .whenPressed(new MoveToHookPositionCommand(this.armSubsystem, this.jointSubsystem, this.clawSubsystem));


        this.actionController.getGamepadButton(GamepadKeys.Button.X)
                .toggleWhenPressed(
                        new AutoHookCloseCommand(this.hookSubsystem, this.jointSubsystem),
                        new JoyStickHookCommand(this.hookSubsystem, this.actionController)
                );

        new Trigger(() -> Math.abs(this.actionController.getLeftY()) > 0.1).whenActive(new JoyStickHookCommand(this.hookSubsystem, this.actionController));
        new Trigger(() -> Math.abs(this.actionController.getRightY()) > 0.1).whenActive(new ArmByJoystickCommand(this.armSubsystem, this.actionController));
    }
}