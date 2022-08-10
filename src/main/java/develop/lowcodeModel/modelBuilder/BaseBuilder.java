package develop.lowcodeModel.modelBuilder;

import develop.lowcodeModel.modelBean.BaseModel;

public interface BaseBuilder<T extends BaseModel> {
    T build();
}
