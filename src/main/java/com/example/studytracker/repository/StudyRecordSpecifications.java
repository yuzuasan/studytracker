package com.example.studytracker.repository;

import com.example.studytracker.dto.studyrecord.StudyRecordSearchCondition;
import com.example.studytracker.entity.StudyRecord;
import com.example.studytracker.entity.Tag;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 学習記録検索のSpecificationクラス
 * <p>
 * 動的クエリを構築するための検索条件定義を提供する。
 * 検索条件はすべてAND結合で適用される。
 * </p>
 */
public class StudyRecordSpecifications {

    /**
     * 検索条件に基づくSpecificationを生成する
     * <p>
     * ユーザーIDは必須（認可制御）であり、それ以外の条件は任意。
     * 各条件がnullでない場合のみ、WHERE句に追加される。
     * </p>
     *
     * @param userId    ユーザーID（必須：認可制御用）
     * @param condition 検索条件（null許容）
     * @return Specification
     */
    public static Specification<StudyRecord> withCondition(
            Long userId,
            StudyRecordSearchCondition condition) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // ユーザーIDは必須（認可制御：自分のデータのみアクセス可能）
            predicates.add(criteriaBuilder.equal(
                    root.get("user").get("id"), userId));

            // 検索条件がnullの場合は、ユーザーIDのみの条件で検索
            if (condition == null) {
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }

            // from: 開始日以上
            if (condition.getFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("studyDate"), condition.getFrom()));
            }

            // to: 終了日以下
            if (condition.getTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("studyDate"), condition.getTo()));
            }

            // tag: タグ名完全一致（INNER JOIN）
            if (StringUtils.hasText(condition.getTag())) {
                Join<StudyRecord, Tag> tagJoin = root.join("tags");
                predicates.add(criteriaBuilder.equal(
                        tagJoin.get("name"), condition.getTag().trim()));
            }

            // keyword: メモ部分一致
            if (StringUtils.hasText(condition.getKeyword())) {
                String keywordPattern = "%" + condition.getKeyword().trim() + "%";
                predicates.add(criteriaBuilder.like(
                        root.get("memo"), keywordPattern));
            }

            // すべての条件をANDで結合
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
