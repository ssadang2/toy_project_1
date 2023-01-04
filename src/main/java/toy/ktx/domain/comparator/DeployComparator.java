package toy.ktx.domain.comparator;

import toy.ktx.domain.Deploy;
import toy.ktx.domain.ktx.Ktx;
import toy.ktx.domain.mugunhwa.Mugunghwa;
import toy.ktx.domain.saemaul.Saemaul;

import java.util.Comparator;

public final class DeployComparator implements Comparator<Deploy> {
    @Override
    public int compare(Deploy deploy1, Deploy deploy2) {
        if (deploy1.getDepartureTime().isBefore(deploy2.getDepartureTime())) {
            return -1;
        }
        else if (deploy1.getDepartureTime().isAfter(deploy2.getDepartureTime())) {
            return 1;
        }
        else {
            if (deploy1.getTrain() instanceof Ktx && deploy2.getTrain() instanceof Mugunghwa) {
                return -1;
            } else if (deploy1.getTrain() instanceof Ktx && deploy2.getTrain() instanceof Saemaul) {
                return -1;
            } else if(deploy1.getTrain() instanceof Mugunghwa && deploy2.getTrain() instanceof Ktx){
                return 1;
            } else if(deploy1.getTrain() instanceof Mugunghwa && deploy2.getTrain() instanceof Saemaul){
                return -1;
            } else if(deploy1.getTrain() instanceof Saemaul && deploy2.getTrain() instanceof Ktx){
                return 1;
            } else if (deploy1.getTrain() instanceof Saemaul && deploy2.getTrain() instanceof Mugunghwa) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
