package il.aircon.model;

import java.util.Currency;

/**
 * Дополнительные параметры при расчете стоимости
 */
public class CalcProperties {
	
	private Long uid;									//				уникальный идентификатор

	private Currency modulesInstallationCost;			//				стоимость установки внутреннего и наружного блоков
	private Float baseTubeLength;						// [метры]		длина магистрали, стоимость которой включена в стоимость установки
	private Currency tubeCost;							// [руб/метр]	стоимость прокладки одного метра магистрали
	private Currency coolantCost;						// [руб/кг]		стоимость хладагента
	private Currency pumpCost;							// [руб]		стоимость дренажной помпы
	
	public Currency getModulesInstallationCost() {
		return modulesInstallationCost;
	}
	public void setModulesInstallationCost(Currency modulesInstallationCost) {
		this.modulesInstallationCost = modulesInstallationCost;
	}
	public float getBaseTubeLength() {
		return baseTubeLength;
	}
	public void setBaseTubeLength(float baseTubeLength) {
		this.baseTubeLength = baseTubeLength;
	}
	public Currency getTubeCost() {
		return tubeCost;
	}
	public void setTubeCost(Currency tubeCost) {
		this.tubeCost = tubeCost;
	}
	public Currency getCoolantCost() {
		return coolantCost;
	}
	public void setCoolantCost(Currency coolantCost) {
		this.coolantCost = coolantCost;
	}
	public Currency getPumpCost() {
		return pumpCost;
	}
	public void setPumpCost(Currency pumpCost) {
		this.pumpCost = pumpCost;
	}
	public Long getUid() {
		return uid;
	}
	public void setUid(Long uid) {
		this.uid = uid;
	}
}
