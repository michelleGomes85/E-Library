package br.elibrary.web.managed;

import br.elibrary.model.User;
import br.elibrary.model.enuns.UserType;
import br.elibrary.model.service.UserService;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

/**
 * Managed Bean responsável pelo cadastro de novos usuários.
 * 
 * <p>
 * Esse bean funciona no escopo de requisição e é utilizado pela página de
 * registro para receber os dados do usuário, validar e enviar ao serviço de
 * persistência.
 * </p>
 */
@Named("registerBean")
@RequestScoped
public class RegisterBean {

    @EJB
    private UserService userSB;

    /** Usuário que será cadastrado. */
    private User newUser = new User();

    /** Mensagem de sucesso ou erro retornada à interface. */
    private String message;

    /**
     * Construtor do bean.
     * 
     * <p>
     * Define o tipo padrão do usuário como STUDENT.
     * </p>
     */
    public RegisterBean() {
        newUser.setType(UserType.STUDENT);
    }

    /**
     * Realiza o processo de registro do usuário.
     * 
     * <p>
     * Tenta persistir o usuário usando o UserService. Em caso de falha,
     * captura exceções específicas e genéricas para informar mensagens adequadas.
     * </p>
     */
    public String doRegister() {
        try {
        
            newUser.setPasswordHash(newUser.getPasswordHash());

            userSB.create(newUser);
            message = "Usuário cadastrado com sucesso! Você já pode fazer login.";
            

        } catch (IllegalArgumentException e) {
            message = e.getMessage();

        } catch (Exception e) {
            message = "Erro ao cadastrar usuário. Tente novamente.";
        }
        
        return null;
    }

    // ==============================
    // Getters e Setters
    // ==============================

    public User getNewUser() {
        return newUser;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Retorna os tipos de usuários disponíveis para exibição na view.
     *
     * @return array de strings com os tipos de usuário
     */
    public String[] getUserTypes() {
        return new String[] { "STUDENT", "TEACHER" };
    }
}
