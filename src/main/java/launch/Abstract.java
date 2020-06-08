package launch;

import java.util.logging.Logger;

public abstract class Abstract {
    
    protected final Logger SYS_LOG;

    public Abstract() {
        this.SYS_LOG = Logger.getLogger(this.getClass().getName());
    }
}
