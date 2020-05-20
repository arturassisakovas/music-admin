package com.mAdmin.model;

import java.util.List;
import java.util.Map;

import com.mAdmin.entity.Employee;
import com.mAdmin.repository.EmployeeRepository;
import com.mAdmin.util.LazyDataModelUtil;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EmployeeLazyDataModel extends LazyDataModel<Employee> {

    
    private static final long serialVersionUID = 1L;

    
    @Autowired
    private EmployeeRepository employeeRepository;

    
    private boolean enabled;

    @Override
    public List<Employee> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        first /= pageSize;

        Page<Employee> employeePage;

        if (sortField != null) {

            Direction direction = LazyDataModelUtil.getDirection(sortOrder);
            employeePage = employeeRepository.findByEnabledEquals(enabled,
                    PageRequest.of(first, pageSize, Sort.by(direction, sortField)));

        } else {

            employeePage = employeeRepository.findByEnabledEqualsOrderByCreatedAtDesc(enabled,
                    PageRequest.of(first, pageSize));
        }
        if (employeePage.getContent().isEmpty() && first > 0) {
            employeePage = employeeRepository.findByEnabledEqualsOrderByCreatedAtDesc(enabled,
                    PageRequest.of(first - 1, pageSize));
        }

        setRowCount((int) employeePage.getTotalElements());

        return employeePage.getContent();
    }

    
    public boolean isEnabled() {
        return enabled;
    }

    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
