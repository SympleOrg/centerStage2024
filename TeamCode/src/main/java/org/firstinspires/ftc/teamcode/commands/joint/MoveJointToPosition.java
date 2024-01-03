package org.firstinspires.ftc.teamcode.commands.joint;

import com.arcrobotics.ftclib.command.InstantCommand;

import org.firstinspires.ftc.teamcode.subsystems.JointSubSystem;

public class MoveJointToPosition extends InstantCommand {
    public MoveJointToPosition(JointSubSystem jointSubSystem, double jointPosition) {
        super(() -> jointSubSystem.moveServo(jointPosition));
    }
}