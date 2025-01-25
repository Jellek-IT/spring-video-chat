package pl.bronikowski.springchat.backendmain.config.repository;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.query.sqm.produce.function.FunctionParameterType;
import org.hibernate.type.StandardBasicTypes;

//Used in RepositoryConfig
@SuppressWarnings("unused")
public class CustomPostgreSQLDialect extends PostgreSQLDialect {
    @Override
    public void initializeFunctionRegistry(FunctionContributions functionContributions) {
        super.initializeFunctionRegistry(functionContributions);
        var registry = functionContributions.getFunctionRegistry();
        var basicTypeRegistry = functionContributions.getTypeConfiguration().getBasicTypeRegistry();
        registry.patternDescriptorBuilder("jsonb_exists", "jsonb_exists(?1,?2)")
                .setExactArgumentCount(2)
                .setParameterTypes(FunctionParameterType.ANY, FunctionParameterType.STRING)
                .setInvariantType(basicTypeRegistry.resolve(StandardBasicTypes.BOOLEAN))
                .register();
        registry.patternDescriptorBuilder("jsonb_exists_all", "jsonb_exists(?1,?2)")
                .setExactArgumentCount(2)
                .setParameterTypes(FunctionParameterType.ANY, FunctionParameterType.ANY)
                .setInvariantType(basicTypeRegistry.resolve(StandardBasicTypes.BOOLEAN))
                .register();
    }
}
