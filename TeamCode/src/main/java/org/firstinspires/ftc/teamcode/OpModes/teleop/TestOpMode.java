package org.firstinspires.ftc.teamcode.OpModes.teleop;

import com.arcrobotics.ftclib.command.CommandOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.RobotController;
import org.firstinspires.ftc.teamcode.commands.arm.HoldArmPositionWithPIDCommand;
import org.firstinspires.ftc.teamcode.subsystems.ArmSubsystem;
import org.firstinspires.ftc.teamcode.util.OpModeType;
import org.firstinspires.ftc.teamcode.util.TeamColor;

@TeleOp(name = "Robot Testing", group = "test")
public class TestOpMode extends CommandOpMode {
    private RobotController robotController;

    @Override
    public void initialize() {
        this.robotController = new RobotController(OpModeType.Testing, hardwareMap, telemetry, gamepad1, gamepad2, TeamColor.RED);

//        ArmSubsystem armSubsystem = new ArmSubsystem(hardwareMap);

//        armSubsystem.setDefaultCommand(new HoldArmPositionWithPIDCommand(armSubsystem));
    }
}
