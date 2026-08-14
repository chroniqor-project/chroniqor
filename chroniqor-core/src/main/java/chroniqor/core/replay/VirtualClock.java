package chroniqor.core.replay;

import java.time.Instant;
import java.util.Objects;

public final class VirtualClock {

    private Instant currentTime;

    public VirtualClock(Instant initialTime) {
        this.currentTime =
                Objects.requireNonNull(
                        initialTime,
                        "Initial virtual time must not be null"
                );
    }

    public Instant now() {
        return currentTime;
    }

    public void advanceTo(Instant targetTime) {
        Objects.requireNonNull(
                targetTime,
                "Target virtual time must not be null"
        );

        if (targetTime.isBefore(currentTime)) {
            throw new IllegalArgumentException(
                    "Virtual clock cannot move backwards"
            );
        }

        currentTime = targetTime;
    }
}
