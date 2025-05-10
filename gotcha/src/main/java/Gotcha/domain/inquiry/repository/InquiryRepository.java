package Gotcha.domain.inquiry.repository;

import Gotcha.domain.inquiry.entity.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    @Query("""
    SELECT i FROM Inquiry i
    WHERE 
        (:keyword IS NULL OR 
            LOWER(i.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
            LOWER(i.content) LIKE LOWER(CONCAT('%', :keyword, '%')))
      AND (:isSolved IS NULL OR i.isSolved = :isSolved)
    """)
    Page<Inquiry> findInquiries(
            @Param("keyword") String keyword,
            @Param("isSolved") Boolean isSolved,
            Pageable pageable
    );
    @Query("""
    SELECT i FROM Inquiry i
    WHERE 
        (:keyword IS NULL OR 
            LOWER(i.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
            LOWER(i.content) LIKE LOWER(CONCAT('%', :keyword, '%')))
      AND (:isSolved IS NULL OR i.isSolved = :isSolved)
      AND (i.writer.id = :userId)
    """)
    Page<Inquiry> findMyInquiries(
            @Param("keyword") String keyword,
            @Param("isSolved") Boolean isSolved,
            Pageable pageable,
            @Param("userId") Long userId);
}
