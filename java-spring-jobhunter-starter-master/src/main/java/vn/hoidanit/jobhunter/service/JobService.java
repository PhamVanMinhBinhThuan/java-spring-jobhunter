package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResUpdateJobDTO;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.SkillRepository;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
    }

    public Optional<Job> fetchJobById(long id) {
        return this.jobRepository.findById(id);
    }

    public ResCreateJobDTO create(Job j) {
        // check skills
        if (j.getSkills() != null) {
            List<Long> reqSkills = j.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            j.setSkills(dbSkills);
        }

        // create job
        Job currrentJob = this.jobRepository.save(j);

        // convert response
        ResCreateJobDTO dto = new ResCreateJobDTO();
        dto.setId(currrentJob.getId());
        dto.setName(currrentJob.getName());
        dto.setSalary(currrentJob.getSalary());
        dto.setQuantity(currrentJob.getQuantity());
        dto.setLocation(currrentJob.getLocation());
        dto.setLevel(currrentJob.getLevel());
        dto.setStartDate(currrentJob.getStartDate());
        dto.setEndDate(currrentJob.getEndDate());
        dto.setActive(currrentJob.isActive());
        dto.setCreateAt(currrentJob.getCreatedAt());
        dto.setCreateBy(currrentJob.getCreatedBy());

        if (currrentJob.getSkills() != null) {
            List<String> skills = currrentJob.getSkills()
                    .stream().map(item -> item.getName())
                    .collect(Collectors.toList());
            dto.setSkills(skills);
        }

        return dto;
    }

    // Cach cua thay --> Nhung se mat truong createdAt, createdBy khi update
    // public ResUpdateJobDTO update(Job j) {
    // // check skills
    // if (j.getSkills() != null) {
    // List<Long> reqSkills = j.getSkills()
    // .stream().map(x -> x.getId())
    // .collect(Collectors.toList());

    // List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
    // j.setSkills(dbSkills);
    // }

    // // update job
    // Job currrentJob = this.jobRepository.save(j);

    // // convert response
    // ResUpdateJobDTO dto = new ResUpdateJobDTO();
    // dto.setId(currrentJob.getId());
    // dto.setName(currrentJob.getName());
    // dto.setSalary(currrentJob.getSalary());
    // dto.setQuantity(currrentJob.getQuantity());
    // dto.setLocation(currrentJob.getLocation());
    // dto.setLevel(currrentJob.getLevel());
    // dto.setStartDate(currrentJob.getStartDate());
    // dto.setEndDate(currrentJob.getEndDate());
    // dto.setActive(currrentJob.isActive());
    // dto.setUpdateAt(currrentJob.getUpdatedAt());
    // dto.setUpdateBy(currrentJob.getUpdatedBy());

    // if (currrentJob.getSkills() != null) {
    // List<String> skills = currrentJob.getSkills()
    // .stream().map(item -> item.getName())
    // .collect(Collectors.toList());
    // dto.setSkills(skills);
    // }

    // return dto;
    // }

    public ResUpdateJobDTO update(Job j) {
        // Fetch job hiện tại từ database
        Job currentJob = this.jobRepository.findById(j.getId())
                .orElseThrow(() -> new RuntimeException("Job not found"));

        // Update chỉ những field cần thiết
        currentJob.setName(j.getName());
        currentJob.setSalary(j.getSalary());
        currentJob.setQuantity(j.getQuantity());
        currentJob.setLocation(j.getLocation());
        currentJob.setLevel(j.getLevel());
        currentJob.setDescription(j.getDescription());
        currentJob.setStartDate(j.getStartDate());
        currentJob.setEndDate(j.getEndDate());
        currentJob.setActive(j.isActive());

        // Check và update skills
        if (j.getSkills() != null) {
            List<Long> reqSkills = j.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            currentJob.setSkills(dbSkills);
        }

        // Update company nếu cần
        if (j.getCompany() != null) {
            currentJob.setCompany(j.getCompany());
        }

        // Save - @PreUpdate sẽ tự động set updatedAt và updatedBy
        Job updatedJob = this.jobRepository.save(currentJob);

        // Convert response
        ResUpdateJobDTO dto = new ResUpdateJobDTO();
        dto.setId(updatedJob.getId());
        dto.setName(updatedJob.getName());
        dto.setSalary(updatedJob.getSalary());
        dto.setQuantity(updatedJob.getQuantity());
        dto.setLocation(updatedJob.getLocation());
        dto.setLevel(updatedJob.getLevel());
        dto.setStartDate(updatedJob.getStartDate());
        dto.setEndDate(updatedJob.getEndDate());
        dto.setActive(updatedJob.isActive());
        dto.setUpdatedAt(updatedJob.getUpdatedAt());
        dto.setUpdatedBy(updatedJob.getUpdatedBy());

        if (updatedJob.getSkills() != null) {
            List<String> skills = updatedJob.getSkills()
                    .stream().map(item -> item.getName())
                    .collect(Collectors.toList());
            dto.setSkills(skills);
        }

        return dto;
    }

    public void delete(long id) {
        this.jobRepository.deleteById(id);
    }

    public ResultPaginationDTO fetchAll(Specification<Job> spec, Pageable pageable) {
        Page<Job> pageJob = this.jobRepository.findAll(spec, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageJob.getTotalPages());
        mt.setTotal(pageJob.getTotalElements());

        rs.setMeta(mt);

        rs.setResult(pageJob.getContent());

        return rs;
    }
}
