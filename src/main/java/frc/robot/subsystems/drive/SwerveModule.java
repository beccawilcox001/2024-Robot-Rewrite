package frc.robot.subsystems.drive;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import frc.robot.subsystems.drive.SwerveModuleIO.SwerveModuleIOInputs;

public class SwerveModule {
    private SwerveModuleState m_desiredState = new SwerveModuleState(0.0, new Rotation2d());
    double chassisAngularOffset;
    SwerveModuleIO swerveModuleIO;

    private final SwerveModuleIOInputs inputs = new SwerveModuleIOInputs();

    // name is used for smart dashboard values to distinguish between modules
    private String name;

    public SwerveModule(SwerveModuleIO io) {

        this.swerveModuleIO = io;
        this.name = io.getName();
        io.setDriveEncoderPosition(0);
        m_desiredState.angle = new Rotation2d(getTurnEncoderPosition());

    }

    public void setDriveEncoderPosition(double position) {
        swerveModuleIO.setDriveEncoderPosition(position);
    }

    public double getDriveEncoderPosition() {
        return swerveModuleIO.getDriveEncoderPosition();
    }

    public double getDriveEncoderSpeedMPS() {
        return swerveModuleIO.getDriveEncoderSpeedMPS();
    }

    public double getTurnEncoderPosition() {
        return swerveModuleIO.getTurnEncoderPosition();
    }

    public void resetEncoders() {
        swerveModuleIO.setDriveEncoderPosition(0);
    }

    public double getDriveBusVoltage() {
        return swerveModuleIO.getDriveBusVoltage();
    }

    public double getDriveOutput() {
        return swerveModuleIO.getDriveOutput();
    }

    public double getTurnBusVoltage() {
        return swerveModuleIO.getTurnBusVoltage();
    }

    public void updateInputs() {
        swerveModuleIO.updateInputs(inputs);
    }

    /**
     * Returns the current state of the module.
     *
     * @return The current state of the module.
     */
    public SwerveModuleState getState() {
        // Apply chassis angular offset to the encoder position to get the position
        // relative to the chassis.
        return new SwerveModuleState(getDriveEncoderSpeedMPS(),
                new Rotation2d((getTurnEncoderPosition()) - swerveModuleIO.getChassisAngularOffset()));
    }

    /**
     * Returns the current position of the module.
     *
     * @return The current position of the module.
     */
    public SwerveModulePosition getPosition() {
        // Apply chassis angular offset to the encoder position to get the position
        // relative to the chassis.
        return new SwerveModulePosition(
                getDriveEncoderPosition(),
                new Rotation2d(getTurnEncoderPosition() - swerveModuleIO.getChassisAngularOffset()));
    }

    /**
     * Sets the desired state for the module.
     *
     * @param desiredState Desired state with speed and angle.
     */
    public void setDesiredState(SwerveModuleState desiredState) {
        // Apply chassis angular offset to the desired state.
        SwerveModuleState correctedDesiredState = new SwerveModuleState();
        correctedDesiredState.speedMetersPerSecond = desiredState.speedMetersPerSecond;
        correctedDesiredState.angle = desiredState.angle
                .plus(Rotation2d.fromRadians(swerveModuleIO.getChassisAngularOffset()));

        // Optimize the reference state to avoid spinning further than 90 degrees.

        SwerveModuleState optimizedDesiredState = SwerveModuleState.optimize(correctedDesiredState,
                new Rotation2d(getTurnEncoderPosition()));
        swerveModuleIO.setDesiredDriveSpeedMPS(optimizedDesiredState.speedMetersPerSecond);
        swerveModuleIO.setDesiredTurnAngle(optimizedDesiredState.angle.getRadians());
        m_desiredState = desiredState;
    }
}
