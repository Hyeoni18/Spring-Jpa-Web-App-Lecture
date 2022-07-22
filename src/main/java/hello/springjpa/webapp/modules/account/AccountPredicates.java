package hello.springjpa.webapp.modules.account;

import com.querydsl.core.types.Predicate;
import hello.springjpa.webapp.modules.tag.Tag;
import hello.springjpa.webapp.modules.zone.Zone;

import java.util.Set;

public class AccountPredicates {
    //우리가 구현할 쿼리
    public static Predicate findByTagsAndZones(Set<Tag> tags, Set<Zone> zones) {
        QAccount account = QAccount.account;
        return account.zones.any().in(zones).and(account.tags.any().in(tags)); // account 중 zone 목록 중 한 개라도 있으면 되고, tag 목록 중 한 개라도 있으면 된다.
    }

}
