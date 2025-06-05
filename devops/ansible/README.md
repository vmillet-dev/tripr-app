# Ansible Deployment for Tripr App

This directory contains Ansible playbooks for deploying the Tripr Spring Boot application to a Raspberry Pi server using K3s (lightweight Kubernetes).

## Structure

```
ansible/
├── ansible.cfg              # Ansible configuration
├── group_vars/
│   └── all.yml              # Centralized variables
├── playbook/
│   ├── main.yml             # Main orchestration playbook
│   ├── install_postgresql.yml # PostgreSQL installation and configuration
│   ├── install_k3s.yml     # K3s and cert-manager installation
│   └── deploy_webapp.yml    # Application deployment
└── templates/
    ├── cert-manager-clusterissuer.yml.j2  # SSL certificate issuer
    ├── k3s-deploy.yml.j2    # Kubernetes deployment manifests
    └── k3s-secret.yml.j2    # Application secrets
```

## Improvements Made

### Robustness
- **Better error handling**: Added proper `failed_when` and `changed_when` conditions
- **Improved module usage**: Replaced shell commands with proper Ansible modules (`kubernetes.core`, `community.postgresql`)
- **Enhanced verification**: Added comprehensive health checks and rollback mechanisms

### Idempotence
- **PostgreSQL**: Added checks for existing privileges and APT sources before making changes
- **K3s**: Improved service status checks and conditional installation
- **Cert-manager**: Added namespace existence checks before installation
- **Kubernetes resources**: Using `kubernetes.core.k8s` module for better state management

### Modularity
- **Centralized variables**: All configuration moved to `group_vars/all.yml`
- **Configurable resources**: Memory, CPU, and probe settings are now variables
- **Comprehensive tagging**: Added tags for selective playbook execution
- **Organized structure**: Clear separation of concerns across playbooks

## Usage

### Full Deployment
```bash
ansible-playbook -i inventory.ini playbook/main.yml
```

### Selective Execution with Tags
```bash
# Install only PostgreSQL
ansible-playbook -i inventory.ini playbook/main.yml --tags postgresql

# Install only K3s and cert-manager
ansible-playbook -i inventory.ini playbook/main.yml --tags k3s,cert-manager

# Deploy only the webapp
ansible-playbook -i inventory.ini playbook/main.yml --tags webapp
```

### Available Tags
- `postgresql`, `database`, `setup` - PostgreSQL installation
- `k3s`, `kubernetes`, `cert-manager` - K3s cluster setup
- `webapp`, `deployment`, `secrets`, `certificates` - Application deployment

## Variables

All variables are centralized in `group_vars/all.yml`. Key configuration includes:

- **Application**: `app_name`, `app_replicas`, `docker_image`
- **Database**: `db_name`, `db_user`, `db_password`, `pg_version`
- **Domain**: `app_domain`, `cert_manager_email`
- **Resources**: `k8s_resources` (CPU/memory limits)
- **Health checks**: `health_check`, `liveness_probe` configurations

## Prerequisites

- Ansible 2.9+
- `kubernetes.core` collection: `ansible-galaxy collection install kubernetes.core`
- `community.postgresql` collection: `ansible-galaxy collection install community.postgresql`
- Target server with SSH access and sudo privileges

## Security

- Secrets are passed via environment variables from GitHub Actions
- No sensitive data is stored in the playbooks
- SSL certificates are automatically managed by cert-manager
- Database access is restricted to pod CIDR ranges

## Key Ansible Best Practices Implemented

- **Use of `kubernetes.core` collection**: All Kubernetes operations now use proper modules instead of shell commands
- **Proper use of `community.postgresql` modules**: Database operations are idempotent and robust
- **Centralized variable management with `group_vars`**: All configuration is organized and reusable
- **Comprehensive tagging for selective execution**: Allows running specific parts of the deployment
- **Proper error handling with `failed_when` and `changed_when`**: Tasks only report changes when actual changes occur
