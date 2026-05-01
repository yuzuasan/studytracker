package com.example.studytracker.service;

import com.example.studytracker.dto.export.ExportCsvRequest;
import com.example.studytracker.dto.studyrecord.StudyRecordSearchCondition;
import com.example.studytracker.entity.StudyRecord;
import com.example.studytracker.repository.StudyRecordRepository;
import com.example.studytracker.repository.StudyRecordSpecifications;
import com.example.studytracker.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * エクスポート関連のビジネスロジックを担当するServiceクラス
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExportService {

    private final CurrentUserProvider currentUserProvider;
    private final StudyRecordRepository studyRecordRepository;

    /**
     * 指定期間の学習記録をCSV形式で出力する
     *
     * 処理フロー:
     * 1. 認証情報からuserId取得
     * 2. パラメータ from / to を取得
     * 3. 学習記録を取得（user_id一致、date BETWEEN from AND to）
     * 4. date昇順でソート
     * 5. CSV文字列生成（ヘッダ: date,subject,minutes,memo）
     * 6. 各レコードを1行ずつ追加
     * 7. CSV文字列を返却
     *
     * @param request CSVエクスポートリクエスト
     * @return CSVデータ
     */
    @Transactional(readOnly = true)
    public String exportToCsv(ExportCsvRequest request) {
        // 1. 認証情報からuserIdを取得
        Long userId = currentUserProvider.getUserId();

        // 2. 検索条件を構築
        StudyRecordSearchCondition condition = StudyRecordSearchCondition.builder()
                .from(request.getFrom())
                .to(request.getTo())
                .build();

        // 3. 学習記録を取得（認可制御：ユーザーIDは必須条件として含まれる）
        // 4. date昇順でソート
        Sort sort = Sort.by("studyDate").ascending();
        List<StudyRecord> studyRecords = studyRecordRepository.findAll(
                StudyRecordSpecifications.withCondition(userId, condition), sort);

        // 5. CSV文字列生成
        StringBuilder csvBuilder = new StringBuilder();

        // ヘッダ行追加
        csvBuilder.append("date,subject,minutes,memo\n");

        // 6. 各レコードを1行ずつ追加
        for (StudyRecord record : studyRecords) {
            csvBuilder.append(formatCsvRow(record));
        }

        log.debug("[{}] exportToCsv result: count={}", this.getClass().getSimpleName(), studyRecords.size());

        // 7. CSV文字列を返却
        return csvBuilder.toString();
    }

    /**
     * StudyRecordをCSV行にフォーマットする
     *
     * @param record 学習記録
     * @return CSV行
     */
    private String formatCsvRow(StudyRecord record) {
        StringBuilder row = new StringBuilder();

        // date
        row.append(record.getStudyDate()).append(",");

        // subject
        row.append(escapeCsvField(record.getSubject())).append(",");

        // minutes
        row.append(record.getStudyMinutes()).append(",");

        // memo
        row.append(escapeCsvField(record.getMemo())).append("\n");

        return row.toString();
    }

    /**
     * CSVフィールドをエスケープする
     * カンマ、改行、ダブルクォートを含む場合はダブルクォートで囲む
     *
     * @param field フィールド値
     * @return エスケープ済みフィールド値
     */
    private String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }

        // カンマ、改行、ダブルクォートを含む場合はエスケープ
        if (field.contains(",") || field.contains("\n") || field.contains("\"")) {
            // ダブルクォートを2つに重ねてエスケープ
            String escaped = field.replace("\"", "\"\"");
            return "\"" + escaped + "\"";
        }

        return field;
    }
}
