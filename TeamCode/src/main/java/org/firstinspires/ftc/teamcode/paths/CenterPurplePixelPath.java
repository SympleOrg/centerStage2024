package org.firstinspires.ftc.teamcode.paths;

import com.arcrobotics.ftclib.command.SequentialCommandGroup;

import org.firstinspires.ftc.teamcode.commands.drivebase.DriveDistanceDriveCommand;
import org.firstinspires.ftc.teamcode.subsystems.DriveBaseSubsystem;

public class CenterPurplePixelPath extends SequentialCommandGroup {
    public CenterPurplePixelPath(DriveBaseSubsystem driveBaseSubsystem) {
        super();

        addCommands(
                new DriveDistanceDriveCommand(driveBaseSubsystem, 0.75f).withTimeout(4500)
        );
    }
}