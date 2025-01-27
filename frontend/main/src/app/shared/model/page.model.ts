export class Page<T> {
	public totalPages!: number;
	public totalElements!: number;
	public size!: number;
	public content!: T[];
	public number!: number;
	public first!: boolean;
	public last!: boolean;
}
